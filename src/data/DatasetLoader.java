package data;

import model.Paper;
import service.PaperManager;

/**
 * Loads a realistic, carefully curated academic dataset of 55 interconnected research papers
 * across 8 core computer science domains with 120+ directed citation relationships.
 */
public class DatasetLoader {

    public static void loadSampleDataset(PaperManager manager) {
        manager.clear();

        // 1. Deep Learning Foundations
        manager.addPaper(new Paper("P101", "Deep Residual Learning for Image Recognition", "He et al.", 2016,
                "Deep Learning", "Introduces residual connections (ResNet) to train ultra-deep neural networks up to 152 layers.", "10.1109/CVPR.2016.90"));
        manager.addPaper(new Paper("P102", "ImageNet Classification with Deep Convolutional Neural Networks", "Krizhevsky et al.", 2012,
                "Deep Learning", "AlexNet breakthrough on ImageNet using GPUs, ReLU, and Dropout.", "10.1145/3065386"));
        manager.addPaper(new Paper("P103", "Adam: A Method for Stochastic Optimization", "Kingma & Ba", 2014,
                "Deep Learning", "Proposes Adam, an adaptive learning rate optimization algorithm for stochastic gradient descent.", "10.48550/arXiv.1412.6980"));
        manager.addPaper(new Paper("P104", "Batch Normalization: Accelerating Deep Network Training", "Ioffe & Szegedy", 2015,
                "Deep Learning", "Introduces Batch Normalization to address internal covariate shift in deep networks.", "10.48550/arXiv.1502.03167"));
        manager.addPaper(new Paper("P105", "Dropout: A Simple Way to Prevent Neural Networks from Overfitting", "Srivastava et al.", 2014,
                "Deep Learning", "Introduces dropout technique to prevent feature co-adaptation during neural network training.", "10.5555/2627435.2670313"));
        manager.addPaper(new Paper("P106", "Learning Representations by Back-Propagating Errors", "Rumelhart, Hinton & Williams", 1986,
                "Deep Learning", "Seminal paper demonstrating generalized backpropagation for multilayer perceptrons.", "10.1038/323533a0"));
        manager.addPaper(new Paper("P107", "Layer Normalization", "Ba, Kiros & Hinton", 2016,
                "Deep Learning", "Introduces LayerNorm for normalizing hidden state dynamics in RNNs and Transformers.", "10.48550/arXiv.1607.06450"));

        // 2. Transformer & LLM Architectures
        manager.addPaper(new Paper("P108", "Attention Is All You Need", "Vaswani et al.", 2017,
                "Transformer & LLMs", "Proposes the Transformer architecture based entirely on multi-head self-attention mechanisms.", "10.48550/arXiv.1706.03762"));
        manager.addPaper(new Paper("P109", "BERT: Pre-training of Deep Bidirectional Transformers for Language Understanding", "Devlin et al.", 2018,
                "Transformer & LLMs", "Introduces bidirectional pre-training for NLP using masked language modeling.", "10.48550/arXiv.1810.04805"));
        manager.addPaper(new Paper("P110", "Language Models are Few-Shot Learners", "Brown et al. (OpenAI)", 2020,
                "Transformer & LLMs", "Introduces GPT-3 (175B params) demonstrating few-shot in-context learning capabilities.", "10.48550/arXiv.2005.14165"));
        manager.addPaper(new Paper("P111", "RoBERTa: A Robustly Optimized BERT Pretraining Approach", "Liu et al.", 2019,
                "Transformer & LLMs", "Shows that BERT was undertrained and proposes optimized hyperparameter recipes.", "10.48550/arXiv.1907.11692"));
        manager.addPaper(new Paper("P112", "Exploring the Limits of Transfer Learning with a Unified Text-to-Text Transformer (T5)", "Raffel et al.", 2020,
                "Transformer & LLMs", "Unifies all NLP tasks into a text-to-text framework using the T5 encoder-decoder model.", "10.48550/arXiv.1910.10683"));
        manager.addPaper(new Paper("P113", "LLaMA: Open and Efficient Foundation Language Models", "Touvron et al. (Meta)", 2023,
                "Transformer & LLMs", "Releases efficient open foundation models trained on publicly available datasets.", "10.48550/arXiv.2302.13971"));
        manager.addPaper(new Paper("P114", "FlashAttention: Fast and Memory-Efficient Exact Attention with IO-Awareness", "Dao et al.", 2022,
                "Transformer & LLMs", "IO-aware exact attention algorithm leveraging GPU SRAM memory hierarchy.", "10.48550/arXiv.2205.14135"));
        manager.addPaper(new Paper("P115", "LoRA: Low-Rank Adaptation of Large Language Models", "Hu et al.", 2021,
                "Transformer & LLMs", "Freezes pretrained weights and injects trainable low-rank decomposition matrices.", "10.48550/arXiv.2106.09685"));

        // 3. Computer Vision & Generative AI
        manager.addPaper(new Paper("P116", "Generative Adversarial Nets", "Goodfellow et al.", 2014,
                "Computer Vision", "Pioneers minimax game framework pitting Generator against Discriminator.", "10.48550/arXiv.1406.2661"));
        manager.addPaper(new Paper("P117", "An Image is Worth 16x16 Words: Transformers for Image Recognition at Scale (ViT)", "Dosovitskiy et al.", 2020,
                "Computer Vision", "Applies standard Transformer directly to image patches for visual recognition.", "10.48550/arXiv.2010.11929"));
        manager.addPaper(new Paper("P118", "You Only Look Once: Unified, Real-Time Object Detection (YOLO)", "Redmon et al.", 2016,
                "Computer Vision", "Frames object detection as a single regression problem for bounding boxes and class probabilities.", "10.1109/CVPR.2016.91"));
        manager.addPaper(new Paper("P119", "Denoising Diffusion Probabilistic Models", "Ho, Jain & Abbeel", 2020,
                "Computer Vision", "Presents high-quality image synthesis using score-based diffusion probabilistic models.", "10.48550/arXiv.2006.11239"));
        manager.addPaper(new Paper("P120", "High-Resolution Image Synthesis with Latent Diffusion Models (Stable Diffusion)", "Rombach et al.", 2022,
                "Computer Vision", "Enables high-resolution generation by training diffusion models in latent space of autoencoders.", "10.1109/CVPR.2022.01042"));
        manager.addPaper(new Paper("P121", "NeRF: Representing Scenes as Neural Radiance Fields for View Synthesis", "Mildenhall et al.", 2020,
                "Computer Vision", "Synthesizes novel views of complex 3D scenes using continuous volumetric neural fields.", "10.1007/978-3-030-58452-8_24"));
        manager.addPaper(new Paper("P122", "Mask R-CNN", "He et al.", 2017,
                "Computer Vision", "Extends Faster R-CNN for pixel-level instance segmentation and bounding box detection.", "10.1109/ICCV.2017.322"));

        // 4. Natural Language Processing Foundations
        manager.addPaper(new Paper("P123", "Efficient Estimation of Word Representations in Vector Space (Word2Vec)", "Mikolov et al.", 2013,
                "Natural Language Processing", "Introduces Skip-Gram and Continuous Bag-of-Words architectures for vector embeddings.", "10.48550/arXiv.1301.3781"));
        manager.addPaper(new Paper("P124", "GloVe: Global Vectors for Word Representation", "Pennington, Socher & Manning", 2014,
                "Natural Language Processing", "Combines global co-occurrence statistics and local context windows.", "10.3115/v1/D14-1162"));
        manager.addPaper(new Paper("P125", "Deep Contextualized Word Representations (ELMo)", "Peters et al.", 2018,
                "Natural Language Processing", "Models complex syntactic and semantic characteristics across linguistic contexts.", "10.48550/arXiv.1802.05365"));
        manager.addPaper(new Paper("P126", "Neural Machine Translation by Jointly Learning to Align and Translate", "Bahdanau, Cho & Bengio", 2014,
                "Natural Language Processing", "Pioneering additive attention mechanism for sequence-to-sequence neural translation.", "10.48550/arXiv.1409.0473"));
        manager.addPaper(new Paper("P127", "Sentence-BERT: Sentence Embeddings using Siamese BERT-Networks", "Reimers & Gurevych", 2019,
                "Natural Language Processing", "Derives semantically meaningful sentence embeddings for fast semantic similarity search.", "10.48550/arXiv.1908.10084"));

        // 5. Reinforcement Learning & AI Alignment
        manager.addPaper(new Paper("P128", "Playing Atari with Deep Reinforcement Learning (DQN)", "Mnih et al. (DeepMind)", 2013,
                "Reinforcement Learning", "First deep learning model to successfully learn control policies directly from video pixels.", "10.48550/arXiv.1312.5602"));
        manager.addPaper(new Paper("P129", "Mastering the Game of Go with Deep Neural Networks and Tree Search", "Silver et al. (AlphaGo)", 2016,
                "Reinforcement Learning", "Combines Monte Carlo Tree Search with deep value and policy networks to defeat world champions.", "10.1038/nature16961"));
        manager.addPaper(new Paper("P130", "Proximal Policy Optimization Algorithms (PPO)", "Schulman et al. (OpenAI)", 2017,
                "Reinforcement Learning", "Introduces clipped surrogate objective function for stable policy gradient optimization.", "10.48550/arXiv.1707.06347"));
        manager.addPaper(new Paper("P131", "Training Language Models to Follow Instructions with Human Feedback (InstructGPT)", "Ouyang et al.", 2022,
                "Reinforcement Learning", "Aligns language models with human intent using RL from Human Feedback (RLHF).", "10.48550/arXiv.2203.02155"));
        manager.addPaper(new Paper("P132", "Soft Actor-Critic: Off-Policy Maximum Entropy Deep Reinforcement Learning", "Haarnoja et al.", 2018,
                "Reinforcement Learning", "Actor-critic algorithm optimizing for both expected reward and policy entropy.", "10.48550/arXiv.1801.01290"));
        manager.addPaper(new Paper("P133", "Continuous Control with Deep Reinforcement Learning (DDPG)", "Lillicrap et al.", 2015,
                "Reinforcement Learning", "Adapts DQN ideas to continuous action spaces using actor-critic architecture.", "10.48550/arXiv.1509.02971"));

        // 6. Cybersecurity & Cryptography
        manager.addPaper(new Paper("P134", "A Method for Obtaining Digital Signatures and Public-Key Cryptosystems", "Rivest, Shamir & Adleman (RSA)", 1978,
                "Cybersecurity", "Invented the foundational RSA public-key cryptographic algorithm based on prime factorization.", "10.1145/359340.359342"));
        manager.addPaper(new Paper("P135", "Explaining and Harnessing Adversarial Examples", "Goodfellow, Shlens & Szegedy", 2014,
                "Cybersecurity", "Demonstrates Fast Gradient Sign Method (FGSM) to fool neural networks with imperceptible perturbations.", "10.48550/arXiv.1412.6572"));
        manager.addPaper(new Paper("P136", "Differential Privacy: A Survey of Results", "Dwork", 2008,
                "Cybersecurity", "Mathematical formulation for quantifying and bounding privacy loss in statistical databases.", "10.1007/978-3-540-79228-4_1"));
        manager.addPaper(new Paper("P137", "Towards Evaluating the Robustness of Neural Networks", "Carlini & Wagner", 2017,
                "Cybersecurity", "Presents the C&W attack to defeat defensive distillation and evaluate neural network robustness.", "10.1109/SP.2017.49"));
        manager.addPaper(new Paper("P138", "Zero Trust Architecture", "Rose et al. (NIST SP 800-207)", 2020,
                "Cybersecurity", "Establishes enterprise security guiding principles under 'never trust, always verify'.", "10.6028/NIST.SP.800-207"));
        manager.addPaper(new Paper("P139", "Practical Byzantine Fault Tolerance (PBFT)", "Castro & Liskov", 1999,
                "Cybersecurity", "State-machine replication algorithm surviving Byzantine faults in asynchronous systems.", "10.1145/571637.571640"));

        // 7. Blockchain & Distributed Ledger Systems
        manager.addPaper(new Paper("P140", "Bitcoin: A Peer-to-Peer Electronic Cash System", "Satoshi Nakamoto", 2008,
                "Blockchain", "First decentralized cryptocurrency using Proof-of-Work and cryptographic hashing chains.", "10.2139/ssrn.3444018"));
        manager.addPaper(new Paper("P141", "Ethereum: A Next-Generation Smart Contract and Decentralized Application Platform", "Vitalik Buterin", 2014,
                "Blockchain", "Introduces Turing-complete programmable blockchain running the Ethereum Virtual Machine (EVM).", "10.5555/ethereum.whitepaper"));
        manager.addPaper(new Paper("P142", "In Search of an Understandable Consensus Algorithm (Raft)", "Ongaro & Ousterhout", 2014,
                "Blockchain", "Presents Raft, an understandable consensus algorithm equivalent to Paxos in fault tolerance.", "10.5555/2643634.2643666"));
        manager.addPaper(new Paper("P143", "Solana: A New Architecture for a High Performance Blockchain", "Yakovenko", 2018,
                "Blockchain", "Introduces Proof of History (PoH) clock before consensus to scale throughput to 50k+ TPS.", "10.5555/solana.whitepaper"));
        manager.addPaper(new Paper("P144", "Flash Boys 2.0: Frontrunning, Transaction Reordering, and MEV in Decentralized Exchanges", "Daian et al.", 2019,
                "Blockchain", "Quantifies Miner Extractable Value (MEV) and structural arbitrage dynamics on Ethereum.", "10.48550/arXiv.1904.05234"));
        manager.addPaper(new Paper("P145", "Uniswap v2 Core", "Adams et al.", 2020,
                "Blockchain", "Presents constant product automated market maker (CPAMM: x * y = k) for decentralized liquidity.", "10.5555/uniswap.v2"));

        // 8. Data Science & Big Data
        manager.addPaper(new Paper("P146", "MapReduce: Simplified Data Processing on Large Clusters", "Dean & Ghemawat (Google)", 2004,
                "Data Science", "Programming model for processing large datasets with parallel distributed algorithms on clusters.", "10.1145/1327452.1327492"));
        manager.addPaper(new Paper("P147", "Resilient Distributed Datasets: A Fault-Tolerant Abstraction for In-Memory Computing (Spark)", "Zaharia et al.", 2012,
                "Data Science", "Introduces RDDs allowing iterative algorithms to keep working data in cluster memory.", "10.5555/2228298.2228301"));
        manager.addPaper(new Paper("P148", "XGBoost: A Scalable Tree Boosting System", "Chen & Guestrin", 2016,
                "Data Science", "End-to-end tree boosting system dominating tabular data science competitions.", "10.1145/2939672.2939785"));
        manager.addPaper(new Paper("P149", "Scikit-learn: Machine Learning in Python", "Pedregosa et al.", 2011,
                "Data Science", "Python library integrating classic machine learning algorithms with unified fit/predict APIs.", "10.5555/1953048.2078195"));
        manager.addPaper(new Paper("P150", "Semi-Supervised Classification with Graph Convolutional Networks (GCN)", "Kipf & Welling", 2016,
                "Data Science", "Scalable approach for semi-supervised learning on graph-structured data using spectral convolutions.", "10.48550/arXiv.1609.02907"));
        manager.addPaper(new Paper("P151", "Inductive Representation Learning on Large Graphs (GraphSAGE)", "Hamilton, Ying & Leskovec", 2017,
                "Data Science", "General inductive framework generating node embeddings by sampling and aggregating local neighborhoods.", "10.48550/arXiv.1706.02216"));
        manager.addPaper(new Paper("P152", "DeepWalk: Online Learning of Social Representations", "Perozzi, Al-Rfou & Skiena", 2014,
                "Data Science", "Uses randomized truncated walks to learn continuous representations for graph vertices.", "10.1145/2623330.2623732"));
        manager.addPaper(new Paper("P153", "LightGBM: A Highly Efficient Gradient Boosting Decision Tree", "Ke et al. (Microsoft)", 2017,
                "Data Science", "Gradient-based One-Side Sampling and Exclusive Feature Bundling for faster GBDT training.", "10.5555/3294996.3295074"));
        manager.addPaper(new Paper("P154", "BERT Meets Information Retrieval", "Nogueira & Cho", 2019,
                "Natural Language Processing", "Demonstrates applying neural transformer representations to document re-ranking.", "10.48550/arXiv.1904.08375"));
        manager.addPaper(new Paper("P155", "Generative Agents: Interactive Simulacra of Human Behavior", "Park et al. (Stanford)", 2023,
                "Artificial Intelligence", "Believable simulacra of human behavior via LLM reflection, memory stream, and planning.", "10.1145/3586183.3606763"));

        // ==========================================
        // CITATION EDGES (A -> B : Paper A cites Paper B)
        // ==========================================

        // Deep Learning Foundations Citations
        manager.addCitation("P101", "P102"); // ResNet cites AlexNet
        manager.addCitation("P101", "P104"); // ResNet cites BatchNorm
        manager.addCitation("P101", "P106"); // ResNet cites Backprop
        manager.addCitation("P102", "P105"); // AlexNet cites Dropout
        manager.addCitation("P102", "P106"); // AlexNet cites Backprop
        manager.addCitation("P103", "P106"); // Adam cites Backprop
        manager.addCitation("P104", "P105"); // BatchNorm cites Dropout
        manager.addCitation("P104", "P106"); // BatchNorm cites Backprop
        manager.addCitation("P105", "P106"); // Dropout cites Backprop
        manager.addCitation("P107", "P104"); // LayerNorm cites BatchNorm

        // Transformer & LLM Citations
        manager.addCitation("P108", "P107"); // Transformer cites LayerNorm
        manager.addCitation("P108", "P103"); // Transformer cites Adam
        manager.addCitation("P108", "P105"); // Transformer cites Dropout
        manager.addCitation("P108", "P126"); // Transformer cites Bahdanau Attention
        manager.addCitation("P109", "P108"); // BERT cites Transformer
        manager.addCitation("P109", "P125"); // BERT cites ELMo
        manager.addCitation("P109", "P123"); // BERT cites Word2Vec
        manager.addCitation("P110", "P108"); // GPT-3 cites Transformer
        manager.addCitation("P110", "P109"); // GPT-3 cites BERT
        manager.addCitation("P110", "P103"); // GPT-3 cites Adam
        manager.addCitation("P111", "P109"); // RoBERTa cites BERT
        manager.addCitation("P111", "P108"); // RoBERTa cites Transformer
        manager.addCitation("P112", "P108"); // T5 cites Transformer
        manager.addCitation("P112", "P109"); // T5 cites BERT
        manager.addCitation("P113", "P108"); // LLaMA cites Transformer
        manager.addCitation("P113", "P110"); // LLaMA cites GPT-3
        manager.addCitation("P113", "P114"); // LLaMA cites FlashAttention
        manager.addCitation("P114", "P108"); // FlashAttention cites Transformer
        manager.addCitation("P115", "P108"); // LoRA cites Transformer
        manager.addCitation("P115", "P110"); // LoRA cites GPT-3

        // Computer Vision Citations
        manager.addCitation("P116", "P102"); // GAN cites AlexNet
        manager.addCitation("P116", "P106"); // GAN cites Backprop
        manager.addCitation("P117", "P108"); // ViT cites Transformer
        manager.addCitation("P117", "P101"); // ViT cites ResNet
        manager.addCitation("P117", "P102"); // ViT cites AlexNet
        manager.addCitation("P118", "P101"); // YOLO cites ResNet
        manager.addCitation("P118", "P102"); // YOLO cites AlexNet
        manager.addCitation("P119", "P116"); // Diffusion cites GAN
        manager.addCitation("P120", "P119"); // Stable Diffusion cites Diffusion
        manager.addCitation("P120", "P108"); // Stable Diffusion cites Transformer
        manager.addCitation("P121", "P101"); // NeRF cites ResNet
        manager.addCitation("P122", "P101"); // Mask R-CNN cites ResNet

        // NLP Citations
        manager.addCitation("P124", "P123"); // GloVe cites Word2Vec
        manager.addCitation("P125", "P123"); // ELMo cites Word2Vec
        manager.addCitation("P126", "P106"); // Bahdanau Attention cites Backprop
        manager.addCitation("P127", "P109"); // Sentence-BERT cites BERT
        manager.addCitation("P127", "P111"); // Sentence-BERT cites RoBERTa

        // Reinforcement Learning Citations
        manager.addCitation("P128", "P102"); // DQN cites AlexNet
        manager.addCitation("P128", "P106"); // DQN cites Backprop
        manager.addCitation("P129", "P128"); // AlphaGo cites DQN
        manager.addCitation("P129", "P101"); // AlphaGo cites ResNet
        manager.addCitation("P130", "P128"); // PPO cites DQN
        manager.addCitation("P131", "P110"); // InstructGPT cites GPT-3
        manager.addCitation("P131", "P130"); // InstructGPT cites PPO
        manager.addCitation("P131", "P108"); // InstructGPT cites Transformer
        manager.addCitation("P132", "P130"); // SAC cites PPO
        manager.addCitation("P133", "P128"); // DDPG cites DQN

        // Cybersecurity Citations
        manager.addCitation("P135", "P102"); // FGSM cites AlexNet
        manager.addCitation("P135", "P106"); // FGSM cites Backprop
        manager.addCitation("P137", "P135"); // C&W cites FGSM
        manager.addCitation("P137", "P101"); // C&W cites ResNet
        manager.addCitation("P138", "P134"); // Zero Trust cites RSA
        manager.addCitation("P139", "P134"); // PBFT cites RSA

        // Blockchain Citations
        manager.addCitation("P140", "P134"); // Bitcoin cites RSA Cryptography
        manager.addCitation("P141", "P140"); // Ethereum cites Bitcoin
        manager.addCitation("P141", "P139"); // Ethereum cites PBFT
        manager.addCitation("P142", "P139"); // Raft cites PBFT
        manager.addCitation("P143", "P140"); // Solana cites Bitcoin
        manager.addCitation("P143", "P141"); // Solana cites Ethereum
        manager.addCitation("P144", "P141"); // MEV cites Ethereum
        manager.addCitation("P145", "P141"); // Uniswap cites Ethereum

        // Data Science & GNN Citations
        manager.addCitation("P147", "P146"); // Spark cites MapReduce
        manager.addCitation("P148", "P147"); // XGBoost cites Spark
        manager.addCitation("P149", "P146"); // Scikit-learn cites MapReduce
        manager.addCitation("P150", "P101"); // GCN cites ResNet
        manager.addCitation("P150", "P152"); // GCN cites DeepWalk
        manager.addCitation("P151", "P150"); // GraphSAGE cites GCN
        manager.addCitation("P151", "P152"); // GraphSAGE cites DeepWalk
        manager.addCitation("P152", "P123"); // DeepWalk cites Word2Vec
        manager.addCitation("P153", "P148"); // LightGBM cites XGBoost
        manager.addCitation("P154", "P109"); // BERT IR cites BERT
        manager.addCitation("P155", "P110"); // Generative Agents cites GPT-3
        manager.addCitation("P155", "P131"); // Generative Agents cites InstructGPT

        // Synchronize all citation counts
        manager.syncAllCitationCounts();
    }
}
