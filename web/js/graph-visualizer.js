/**
 * CitationFlow: Interactive Citation Network Graph Visualizer
 * Powered by Cytoscape.js
 */

const GraphVisualizer = {
    cy: null,
    topicColors: {
        "Deep Learning": "#3b82f6",
        "Transformer & LLMs": "#8b5cf6",
        "Computer Vision": "#ec4899",
        "Natural Language Processing": "#10b981",
        "Reinforcement Learning": "#f59e0b",
        "Cybersecurity": "#ef4444",
        "Blockchain": "#06b6d4",
        "Data Science": "#6366f1",
        "Artificial Intelligence": "#14b8a6",
        "General Computer Science": "#94a3b8"
    },

    init: function(containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;

        if (typeof cytoscape === "undefined") {
            console.warn("Cytoscape.js not loaded, attempting fallback or CDN.");
            return;
        }

        this.cy = cytoscape({
            container: container,
            style: [
                {
                    selector: 'node',
                    style: {
                        'label': 'data(id)',
                        'color': '#ffffff',
                        'text-valign': 'center',
                        'text-halign': 'center',
                        'font-size': '11px',
                        'font-weight': 'bold',
                        'background-color': 'data(color)',
                        'width': 'mapData(citations, 0, 15, 26, 60)',
                        'height': 'mapData(citations, 0, 15, 26, 60)',
                        'border-width': 2,
                        'border-color': '#ffffff',
                        'transition-property': 'background-color, border-color, width, height',
                        'transition-duration': '0.3s'
                    }
                },
                {
                    selector: 'node:selected',
                    style: {
                        'border-width': 4,
                        'border-color': '#f59e0b',
                        'shadow-blur': 15,
                        'shadow-color': '#f59e0b',
                        'shadow-opacity': 0.8
                    }
                },
                {
                    selector: 'edge',
                    style: {
                        'width': 1.8,
                        'line-color': '#475569',
                        'target-arrow-color': '#475569',
                        'target-arrow-shape': 'triangle',
                        'curve-style': 'bezier',
                        'arrow-scale': 1.1,
                        'opacity': 0.6,
                        'transition-property': 'line-color, target-arrow-color, width, opacity',
                        'transition-duration': '0.3s'
                    }
                },
                {
                    selector: 'node.highlighted',
                    style: {
                        'border-color': '#10b981',
                        'border-width': 4,
                        'shadow-blur': 20,
                        'shadow-color': '#10b981',
                        'shadow-opacity': 0.9
                    }
                },
                {
                    selector: 'node.path-node',
                    style: {
                        'border-color': '#f59e0b',
                        'border-width': 5,
                        'background-color': '#d97706',
                        'shadow-blur': 25,
                        'shadow-color': '#f59e0b',
                        'shadow-opacity': 1.0
                    }
                },
                {
                    selector: 'edge.path-edge',
                    style: {
                        'width': 4,
                        'line-color': '#f59e0b',
                        'target-arrow-color': '#f59e0b',
                        'opacity': 1.0,
                        'z-index': 999
                    }
                },
                {
                    selector: 'node.active-trace',
                    style: {
                        'border-color': '#38bdf8',
                        'border-width': 6,
                        'shadow-blur': 30,
                        'shadow-color': '#38bdf8',
                        'shadow-opacity': 1.0
                    }
                }
            ],
            layout: {
                name: 'cose',
                animate: false,
                randomize: false,
                nodeRepulsion: 450000,
                idealEdgeLength: 100,
                nodeOverlap: 20
            }
        });

        // Click handler to inspect paper
        this.cy.on('tap', 'node', (evt) => {
            const paperId = evt.target.id();
            if (window.App && window.App.inspectPaper) {
                window.App.inspectPaper(paperId);
            }
        });
    },

    renderGraph: function(graphData, papersList) {
        if (!this.cy) return;

        const paperMap = {};
        if (papersList) {
            papersList.forEach(p => { paperMap[p.id] = p; });
        }

        const elements = [];

        // Nodes
        graphData.nodes.forEach(node => {
            const paper = paperMap[node.id];
            const topic = paper ? paper.topic : "General Computer Science";
            const color = this.topicColors[topic] || "#64748b";
            const title = paper ? paper.title : node.id;
            const citations = node.inDegree || (paper ? paper.inCitationCount : 0);

            elements.push({
                group: 'nodes',
                data: {
                    id: node.id,
                    title: title,
                    topic: topic,
                    color: color,
                    citations: citations
                }
            });
        });

        // Edges
        graphData.edges.forEach(edge => {
            elements.push({
                group: 'edges',
                data: {
                    id: edge.id || `e_${edge.source}_${edge.target}`,
                    source: edge.source,
                    target: edge.target
                }
            });
        });

        this.cy.elements().remove();
        this.cy.add(elements);
        this.applyLayout('cose');
    },

    applyLayout: function(layoutName) {
        if (!this.cy) return;
        const options = {
            name: layoutName || 'cose',
            animate: true,
            animationDuration: 600,
            padding: 30
        };

        if (layoutName === 'concentric') {
            options.concentric = (node) => node.data('citations') || 1;
            options.levelWidth = () => 2;
        } else if (layoutName === 'breadthfirst') {
            options.directed = true;
            options.spacingFactor = 1.2;
        }

        this.cy.layout(options).run();
    },

    highlightPath: function(pathNodeIds) {
        if (!this.cy || !pathNodeIds || pathNodeIds.length === 0) return;

        this.clearHighlights();

        pathNodeIds.forEach(id => {
            const node = this.cy.getElementById(id);
            if (node) node.addClass('path-node');
        });

        for (let i = 0; i < pathNodeIds.length - 1; i++) {
            const src = pathNodeIds[i];
            const tgt = pathNodeIds[i + 1];
            const edge = this.cy.edges(`[source = "${src}"][target = "${tgt}"]`);
            if (edge) edge.addClass('path-edge');
        }

        // Center view on path
        const pathElements = this.cy.collection();
        pathNodeIds.forEach(id => pathElements.merge(this.cy.getElementById(id)));
        this.cy.animate({
            fit: { eles: pathElements, padding: 50 },
            duration: 500
        });
    },

    highlightActiveTraceNode: function(nodeId) {
        if (!this.cy) return;
        this.cy.nodes().removeClass('active-trace');
        if (nodeId) {
            const node = this.cy.getElementById(nodeId);
            if (node) {
                node.addClass('active-trace');
            }
        }
    },

    focusNode: function(nodeId) {
        if (!this.cy) return;
        const node = this.cy.getElementById(nodeId);
        if (node.length > 0) {
            this.cy.animate({
                center: { eles: node },
                zoom: 1.6,
                duration: 500
            });
            node.select();
        }
    },

    clearHighlights: function() {
        if (!this.cy) return;
        this.cy.elements().removeClass('highlighted path-node path-edge active-trace');
    },

    resetView: function() {
        if (!this.cy) return;
        this.clearHighlights();
        this.cy.animate({
            fit: { padding: 30 },
            duration: 400
        });
    }
};

window.GraphVisualizer = GraphVisualizer;
