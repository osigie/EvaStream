# EvaStream

A hybrid peer-to-peer streaming system built with Java and Spring Boot. HStream reduces origin server bandwidth by enabling peers to serve chunks to one another, while falling back to a central origin server when needed.

---

## Architecture Overview

```
┌─────────────────┐     ┌─────────────────┐
│ Metadata Service│     │ Tracker Service │
│ Spring Boot + H2│     │ Spring Boot     │
└────────┬────────┘     └────────┬────────┘
         │                       │
         └──────────┬────────────┘
                    │
         ┌──────────▼────────────┐
         │     Peer Node(s)      │
         │  (Download + Upload)  │
         └──────────┬────────────┘
                    │ fallback
         ┌──────────▼────────────┐
         │    Origin Server      │
         │  (Chunk HTTP Store)   │
         └───────────────────────┘
```

**System Style:** Hybrid P2P with Central Origin Fallback

---

## Services

| Service          | Port (default) | Tech                            |
| ---------------- | -------------- | ------------------------------- |
| Metadata Service | `8081`         | Spring Boot, H2, JPA            |
| Tracker Service  | `8082`         | Spring Boot, in-memory          |
| Origin Server    | `8083`         | Spring Boot, file storage       |
| Peer Node(s)     | dynamic        | Spring Boot + TCP upload server |

---

## Phases

### Phase 1 – Metadata Service

Stores song metadata and chunk hashes, and provides them to peers on demand.

**Data Model:**

`Song` — `id`, `title`, `fileSize`, `chunkSize`, `chunkCount`

`SongChunk` — `songId`, `chunkId`, `hash`

**Endpoints:**

```
GET    /metadata/{songId}   → returns song + chunk metadata
POST   /metadata            → register new song
DELETE /metadata/{songId}   → remove song
```

---

### Phase 2 – Origin Server

Stores full songs pre-split into fixed-size binary chunks and serves them over HTTP.

**Storage Layout:**

```
/origin-storage/
  /song-1/
    chunk-0.bin
    chunk-1.bin
    ...
```

**Endpoint:**

```
GET /chunk?songId=X&chunkId=Y  → returns raw chunk bytes
```

**Preprocessing Utility:**

A standalone utility reads an MP3, splits it into fixed-size chunks, computes a SHA-256 hash per chunk, writes the `.bin` files, and registers the metadata with the Metadata Service.

---

### Phase 3 – Tracker Service

Maintains live peer state in memory. Peers register, send heartbeats, and advertise which chunks they hold.

**In-Memory Structures:**

```
peerRegistry:    Map<peerId, PeerInfo>
songChunkMap:    Map<songId, Map<chunkId, Set<peerId>>>
```

**Endpoints:**

```
POST /register           → register a new peer
POST /heartbeat          → keep peer alive
GET  /peers?songId=X     → get peers and their chunks for a song
POST /chunk-acquired     → notify tracker of a newly downloaded chunk
```

**Background Task:** Every 30 seconds, the tracker evicts peers whose heartbeat has expired and cleans their chunk entries from the map.

---

### Phase 4 – Peer Node

The core participant in the swarm. Each peer registers with the tracker, downloads chunks preferring other peers, serves chunks via a TCP upload server, and maintains a local chunk store.

**Internal Modules:**

| Module                | Responsibility                                              |
| --------------------- | ----------------------------------------------------------- |
| `TrackerClient`       | register, heartbeat, getPeers, notifyChunkAcquired          |
| `MetadataClient`      | fetch song + chunk metadata                                 |
| `OriginClient`        | fetch chunk bytes from origin as fallback                   |
| `UploadServer` (TCP)  | accept socket connections, serve chunk bytes to other peers |
| `ChunkStore`          | saveChunk, hasChunk, loadChunk on local disk                |
| `DownloadCoordinator` | build download plan, manage parallel downloads              |
| `Scheduler`           | rarest-first ordering, deduplication of in-flight chunks    |

---

### Phase 5 – Hybrid Download Logic

For every chunk in the song:

1. Ask the tracker for peers that hold the chunk.
2. Try each peer in order — connect via TCP and request the chunk.
3. If a peer fails, move to the next candidate.
4. If all peers fail, fall back to the origin server over HTTP.
5. Persist the chunk locally via `ChunkStore`.
6. Notify the tracker via `POST /chunk-acquired`.

Chunk selection order follows a **rarest-first** strategy — chunks held by the fewest peers are prioritized to increase swarm diversity.

---

### Phase 6 – Concurrency Model

Each peer node runs the following concurrent components:

- **Upload server thread** — listens for incoming TCP chunk requests
- **Heartbeat thread** — pings the tracker on a fixed interval
- **Scheduler thread** — selects and queues chunks for download
- **Worker thread pool** — executes parallel chunk downloads (configurable max)

Both concurrent uploads and downloads are bounded to prevent resource exhaustion.

---

### Phase 7 – Failure Handling

| Failure                        | Response                                     |
| ------------------------------ | -------------------------------------------- |
| Peer crash                     | Tracker removes peer after heartbeat timeout |
| Chunk download fails from peer | Retry next available peer                    |
| All peers fail for a chunk     | Fall back to origin server                   |
| Origin server fails            | Retry with exponential backoff               |

---

---

## Running the System

Start services in this order:

```bash
# 1. Metadata Service
cd metadata-service && mvn spring-boot:run

# 2. Tracker Service
cd tracker-service && mvn spring-boot:run

# 3. Origin Server (after preprocessing your MP3s)
cd origin-server && mvn spring-boot:run

# 4. Peer Nodes (run 3 or more)
cd peer-node && mvn spring-boot:run -Dspring-boot.run.arguments="--peer.id=peer-1 --peer.port=9001"
cd peer-node && mvn spring-boot:run -Dspring-boot.run.arguments="--peer.id=peer-2 --peer.port=9002"
cd peer-node && mvn spring-boot:run -Dspring-boot.run.arguments="--peer.id=peer-3 --peer.port=9003"
```

**Preprocessing a song:**

```bash
java -jar preprocessor.jar --input=song.mp3 --songId=song-1 --chunkSize=262144
```

---

## Expected Demo Behavior

| Observation                             | Explanation                                       |
| --------------------------------------- | ------------------------------------------------- |
| First peer downloads mostly from origin | No other peers hold chunks yet                    |
| Later peers download mostly from peers  | The first peer has built up a chunk store         |
| Origin bandwidth decreases over time    | The swarm absorbs more of the load                |
| Swarm becomes self-sustaining           | Peers serve each other without origin involvement |

---

## Tech Stack

- **Language:** Java 17+
- **Framework:** Spring Boot 3.x
- **Database:** H2 (Metadata Service)
- **Build:** Maven
- **Persistence:** Spring Data JPA
- **Transport:** HTTP (REST) + raw TCP sockets (peer uploads)
- **Hashing:** SHA-256 (chunk integrity)
