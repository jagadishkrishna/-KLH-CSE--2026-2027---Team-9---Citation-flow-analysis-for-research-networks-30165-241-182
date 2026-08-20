# CitationFlow: Citation Flow Analysis for Research Networks

[![Java](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://www.oracle.com/java/)
[![DSA](https://img.shields.io/badge/DSA-Directed%20Graphs%20%7C%20Max--Heap%20%7C%20BFS%20%7C%20DFS-orange.svg)]()
[![Status](https://img.shields.io/badge/Tests-64%2F64%20Passing-brightgreen.svg)]()
[![License](https://img.shields.io/badge/Academic-B.Tech%20CSE%20DSA-purple.svg)]()

> **CitationFlow** is a comprehensive Data Structures and Algorithms (DSA) college-level software system that models academic research papers and their asymmetric citation relationships as a **Directed Graph ($V, E$)**. 
> Built in pure Java 17+ with zero external framework dependencies, it pairs custom graph algorithms, max-heaps, and divide-and-conquer sorting with an interactive academic web analytics platform.

---

## 1. Project Objective

In scientific literature, research papers reference earlier foundational work. By modeling papers as **vertices** ($V$) and citation references as **directed edges** ($E$, where $A \rightarrow B$ means Paper $A$ cites Paper $B$), CitationFlow analyzes:
- **Citation Lineage & Traversal**: Tracking how seminal ideas flow down to modern state-of-the-art architectures using Breadth-First Search (BFS) and Depth-First Search (DFS).
- **Influence & Rankings**: Identifying top-tier seminal papers in $O(k \log n)$ time using a custom **Binary Max-Heap Priority Queue**.
- **Shortest Citation Paths**: Computing the minimum reference hops connecting two research publications.
- **Structural Integrity**: Detecting circular citation loops and computing academic impact metrics such as network $h$-index and graph density.

---

## 2. Problem Statement & Proposed Solution

### The Problem
Academic research networks contain millions of interconnected papers. Traditional database queries struggle to efficiently answer graph queries such as:
1. *What are the citation levels branching from a seed paper?*
2. *What is the shortest chain of references between two distant discoveries?*
3. *Which papers are the most influential without sorting the entire dataset $O(n \log n)$ on every top-$k$ query?*

### The Proposed Solution
CitationFlow implements an **in-memory Adjacency List Directed Graph** combined with:
- **$O(1)$ HashMap Indexing** for instant paper metadata retrieval.
- **Level-Order BFS** with predecessor map for shortest path discovery.
- **Recursive 3-Color DFS** for research chain tracing and cycle detection.
- **Custom Array-Based Binary Max-Heap** for $O(1)$ max peek and $O(\log n)$ extract-max.
- **Merge Sort & Quick Sort** for benchmarked multi-attribute sorting.

---

## 3. Technology Stack

| Layer | Technology | Details |
|---|---|---|
| **Language** | Java 17+ / Java 25 | Object-Oriented Architecture, Strict Encapsulation |
| **Data Structures** | Custom Java Implementations | Adjacency List, Max-Heap, Queue, Stack, Hash Table |
| **Web Server** | Java `com.sun.net.httpserver` | Built-in zero-dependency standard library HTTP/REST server |
| **Frontend UI** | HTML5, CSS3, JavaScript (ES6) | Responsive Single-Page Application (SPA) |
| **Graph Visualizer** | Cytoscape.js | Dynamic directed graph canvas with layout physics |
| **Analytics Charts** | Chart.js | Publication trends and citation volume distributions |
| **Dataset** | In-Memory / JSON | 55 realistic papers across 8 CS domains with 120+ edges |

---

## 4. DSA Concepts & Complexity Analysis

### Core Data Structures

| Data Structure | Implementation | Purpose in CitationFlow | Time Complexity | Space Complexity |
|---|---|---|---|---|
| **Directed Graph** | Adjacency List (`HashMap<String, Set<String>>`) | Models asymmetric citations ($A \rightarrow B$) | Vertex Add: $O(1)$<br>Edge Add: $O(1)$ | $O(V + E)$ |
| **HashMap** | `LinkedHashMap<String, Paper>` | $O(1)$ average ID lookups and fast filtering | Avg: $O(1)$, Worst: $O(n)$ | $O(V)$ |
| **Queue (FIFO)** | `ArrayDeque<String>` | BFS level-order exploration and shortest path | Enqueue: $O(1)$<br>Dequeue: $O(1)$ | $O(V)$ |
| **Call Stack** | Recursion Stack / Active Set | DFS lineage exploration and cycle detection | Push: $O(1)$<br>Pop: $O(1)$ | $O(V)$ |
| **Binary Max-Heap** | Custom `ArrayList<Paper>` | Priority Queue for Top-$K$ Most Cited Papers | Build: $O(n)$<br>Extract: $O(\log n)$ | $O(n)$ |
| **Merge Sort** | Custom Recursive Divide & Conquer | Stable multi-criteria sorting (citations, year) | $O(n \log n)$ all cases | $O(n)$ |
| **Quick Sort** | Custom In-Place Partitioning (Lomuto) | Low-overhead in-place sorting benchmark | Avg: $O(n \log n)$<br>Worst: $O(n^2)$ | $O(\log n)$ |

---

### Core Algorithms Detailed Breakdown

#### 1. Breadth-First Search (BFS) — Citation Levels & Reachability
- **Data Structure**: Queue (FIFO) + Visited Set (Hash Set) + Distance Map.
- **Workflow**:
  1. Enqueue source paper at Level 0 and mark visited.
  2. While queue is non-empty, dequeue vertex $u$.
  3. For each unvisited neighbor $v \in \text{outNeighbors}(u)$, set $\text{level}[v] = \text{level}[u] + 1$, mark visited, and enqueue $v$.
- **Time Complexity**: $O(V + E)$
- **Space Complexity**: $O(V)$

#### 2. Depth-First Search (DFS) & Cycle Detection
- **Data Structure**: Call Stack + 3-Color State Array (0: White/Unvisited, 1: Gray/In Stack, 2: Black/Finished).
- **Workflow**:
  - Traverses deep down research lineage paths.
  - If a neighbor is encountered that is currently **Gray (in the active recursion stack)**, a **back-edge** is detected, proving the existence of a circular citation loop.
- **Time Complexity**: $O(V + E)$
- **Space Complexity**: $O(V)$

#### 3. Shortest Citation Path Finder
- **Data Structure**: BFS + Predecessor Map (`HashMap<String, String>`).
- **Justification**: In an unweighted directed graph, BFS guarantees finding the minimum number of edge hops to the target. Once the target is reached, parent pointers are backtracked from target to source.
- **Time Complexity**: $O(V + E)$

#### 4. Binary Max-Heap (Priority Queue) Ranking
- **Invariant**: For node at index $i$, $\text{Parent}(i) = \lfloor (i - 1)/2 \rfloor$. The root holds the maximum citations.
- **Build Heap**: Bottom-up heapification starting from $\lfloor n/2 \rfloor - 1$ down to 0 in $O(n)$ time.
- **Extract Max**: Replaces root with last element and calls `heapifyDown(0)` in $O(\log n)$ time.

---

## 5. System Architecture & Class Hierarchy

```
CitationFlow/
├── src/
│   ├── model/
│   │   ├── Paper.java                     # Vertex entity: ID, title, author, year, topic, in/out degrees
│   │   └── TraversalResult.java           # Step-by-step trace logger for BFS, DFS, and Path
│   ├── dsa/
│   │   ├── CitationGraph.java             # Directed Graph with forward/reverse adjacency lists
│   │   ├── GraphAlgorithms.java           # BFS, DFS, Shortest Path, 3-Color Cycle Detection
│   │   ├── CustomMaxHeap.java             # Pure Binary Max-Heap priority queue implementation
│   │   ├── MergeSort.java                 # Custom Merge Sort implementation
│   │   └── QuickSort.java                 # Custom Quick Sort implementation
│   ├── service/
│   │   ├── PaperManager.java              # O(1) HashMap lookup, multi-field search, graph sync
│   │   ├── CitationAnalyzer.java          # Graph density, h-index, metrics, distributions
│   │   └── RankingManager.java            # Max-Heap top-K orchestrator & sort benchmarks
│   ├── data/
│   │   └── DatasetLoader.java             # Preloaded 55 interconnected papers across 8 domains
│   ├── server/
│   │   ├── SimpleHttpServer.java          # com.sun.net.httpserver REST API + static files
│   │   └── ApiHandler.java                # JSON REST request router and controller
│   ├── test/
│   │   └── TestRunner.java                # 64 automated unit & algorithm test cases
│   └── Main.java                          # CLI demonstration & server launcher
├── web/
│   ├── index.html                         # Responsive Single-Page Academic UI
│   ├── css/style.css                      # Modern academic styling
│   └── js/
│       ├── app.js                         # State manager, REST caller, event coordinator
│       ├── graph-visualizer.js            # Cytoscape.js directed graph renderer
│       ├── dsa-tracer.js                  # Animated step-by-step BFS/DFS & Heap tracer
│       └── charts.js                      # Chart.js analytics & trend distributions
├── data/
│   └── papers_dataset.json                # Offline JSON dataset backup
├── run.bat                                # 1-click Windows runner
├── run.sh                                 # 1-click Unix/Linux/macOS runner
└── README.md                              # Complete project documentation
```

---

## 6. Academic Dataset Overview

The system includes **55 foundational and modern research papers** spanning:
1. **Deep Learning Foundations** (AlexNet, ResNet, Backprop, BatchNorm, Dropout, Adam, LayerNorm)
2. **Transformer & LLMs** (Attention Is All You Need, BERT, GPT-3, RoBERTa, T5, LLaMA, FlashAttention, LoRA)
3. **Computer Vision & Generative AI** (GANs, ViT, YOLO, Diffusion Models, Stable Diffusion, NeRF, Mask R-CNN)
4. **Natural Language Processing** (Word2Vec, GloVe, ELMo, Sentence-BERT, Bahdanau Attention)
5. **Reinforcement Learning & AI Alignment** (DQN, AlphaGo, PPO, InstructGPT/RLHF, SAC, DDPG)
6. **Cybersecurity & Cryptography** (RSA Algorithm, FGSM Adversarial Attacks, Differential Privacy, Zero Trust, PBFT)
7. **Blockchain & Distributed Ledgers** (Bitcoin Whitepaper, Ethereum EVM, Raft Consensus, Solana, MEV, Uniswap)
8. **Data Science & Graph Neural Networks** (MapReduce, Spark, XGBoost, Scikit-learn, GCN, GraphSAGE, DeepWalk, LightGBM)

---

## 7. Installation & How to Run

### Prerequisites
- **Java Development Kit (JDK 17 or higher)** installed and available in your `PATH`.
- Modern web browser (Chrome, Edge, Firefox, Safari).

### Quick 1-Click Launch (Windows)
Double-click `run.bat` or run in CMD / PowerShell:
```cmd
run.bat
```

### Quick 1-Click Launch (Linux / macOS)
```bash
chmod +x run.sh
./run.sh
```

### Manual Compilation & Execution
1. **Compile all Java files:**
   ```bash
   javac -d bin src/model/*.java src/dsa/*.java src/data/*.java src/service/*.java src/server/*.java src/test/*.java src/Main.java
   ```

2. **Run the Automated Test Suite (64/64 tests):**
   ```bash
   java -cp bin test.TestRunner
   ```

3. **Start the Web Application:**
   ```bash
   java -cp bin Main 8080
   ```
   Open your browser at: **`http://localhost:8080/index.html`**

4. **Run in Interactive Terminal CLI Mode (for Viva Demo):**
   ```bash
   java -cp bin Main --cli
   ```

---

## 8. Academic Viva Preparation Guide (Problem $\rightarrow$ DS $\rightarrow$ Algo $\rightarrow$ Complexity)

Use this cheatsheet for viva and professor questions:

| Feature / Problem | Underlying Data Structure | Algorithm Applied | Implementation Details | Output Produced | Complexity |
|---|---|---|---|---|---|
| **Shortest Citation Path** | Directed Graph + Queue + Predecessor Map | Breadth-First Search (BFS) | Visits neighbors in order of increasing distance | Minimum-hop citation sequence (e.g. $P108 \rightarrow P107 \rightarrow P104 \rightarrow P106$) | **Time:** $O(V + E)$<br>**Space:** $O(V)$ |
| **Citation Level Discovery** | Directed Graph + FIFO Queue | Level-Order BFS Traversal | Enqueues unvisited cited papers level-by-level | Map of Paper IDs to citation distance from root | **Time:** $O(V + E)$<br>**Space:** $O(V)$ |
| **Research Influence Chains** | Directed Graph + Call Stack | Depth-First Search (DFS) | Recursively steps into outgoing citation paths | Deep lineage traversal sequence | **Time:** $O(V + E)$<br>**Space:** $O(V)$ |
| **Citation Cycle Detection** | Directed Graph + 3-Color Map | DFS Back-Edge Detection | Tracks active recursion stack nodes (Gray) | Boolean cycle flag and loop path | **Time:** $O(V + E)$<br>**Space:** $O(V)$ |
| **Top-K Influential Papers** | Binary Max-Heap (Priority Queue) | Bottom-up Build-Heap & `extractMax` | Array-based binary tree with `heapifyDown` | Top-$K$ highest cited papers | **Time:** $O(n + k \log n)$<br>**Space:** $O(n)$ |
| **Paper Sorting Benchmark** | Dynamic Array | Custom Merge Sort & Quick Sort | Recursive divide & conquer partitioning | Sorted paper list by citations, year, or title | **Time:** $O(n \log n)$<br>**Space:** $O(n)$ / $O(\log n)$ |
| **Instant Paper Retrieval** | Hash Table (`HashMap`) | Hash Code & Bucket Lookup | Keyed by Paper ID | Single paper details in average $O(1)$ | **Time:** Avg $O(1)$, Worst $O(n)$ |

---

## 9. Automated Testing Results

The test suite in `src/test/TestRunner.java` automatically runs 64 comprehensive assertions covering all edge cases:

```
==================================================================
   RUNNING CITATIONFLOW DSA COMPREHENSIVE TEST SUITE
==================================================================
--- Testing Paper Model ---
  [PASS] Paper ID capitalized
  [PASS] Title match
  [PASS] Initial in-citation count is 0
  [PASS] Increment in-citation count to 1
  [PASS] Decrement in-citation count to 0
  [PASS] In-citation count cannot drop below 0
--- Testing Directed Graph Operations ---
  [PASS] Add Paper A, B, C
  [PASS] Prevent duplicate Paper A
  [PASS] Graph vertex count is 3
  [PASS] Add citation A -> B, A -> C, B -> C
  [PASS] Check citation exists A -> B
  [PASS] Check citation does not exist B -> A
  [PASS] A out-degree is 2, C in-degree is 2
--- Testing Citation Validations ---
  [PASS] Prevent self-citation A -> A
  [PASS] Prevent citation to nonexistent target
  [PASS] Prevent duplicate citation edge A -> B
--- Testing Paper Deletion with Cascade Edge Cleanup ---
  [PASS] Delete Paper B with cascade edge removal
--- Testing Breadth-First Search (BFS) ---
  [PASS] BFS visits P1 first & all reachable nodes
  [PASS] Level calculations (P1=0, P2=1, P4=2)
--- Testing Depth-First Search (DFS) ---
  [PASS] DFS visits sequence & DAG acyclic check
--- Testing Directed Graph Cycle Detection ---
  [PASS] Acyclic graph has no cycle
  [PASS] Cyclic graph detected by 3-color DFS
--- Testing Shortest Citation Path (BFS) ---
  [PASS] Shortest path [A, E, D] with 2 hops
--- Testing Custom Binary Max-Heap ---
  [PASS] Peek returns max element (P4 with 90)
  [PASS] Successive extractMax in descending order
  [PASS] Heap empty check
--- Testing Custom Merge Sort & Quick Sort ---
  [PASS] MergeSort descending citations correctness
  [PASS] QuickSort title alphabetical correctness
--- Testing PaperManager Multi-Search & HashMap ---
  [PASS] O(1) HashMap lookup for P101
  [PASS] Multi-criteria search and topic filtering
--- Testing Citation Analyzer Graph Metrics ---
  [PASS] Total papers, citations, density, h-index
--- Testing Graph & Algorithm Edge Cases ---
  [PASS] Empty graph, disconnected graph, null lookups
==================================================================
   TEST SUMMARY: 64 / 64 TESTS PASSED (100.0%)
==================================================================
```

---

## 10. Future Enhancements

1. **Graph Neural Network (GNN) Node Embeddings**: Implementing in-browser spectral clustering for automated research subfield discovery.
2. **PageRank / HITS Algorithm**: Augmenting simple citation count with link-weighted PageRank to distinguish authoritative seminal papers from review papers.
3. **Live Semantic Scholar API Integration**: Optional sync module to import live ArXiv and CrossRef citations dynamically.
