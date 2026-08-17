/**
 * CitationFlow: Core Application Controller & State Manager
 */

const App = {
    apiBase: '',
    allPapers: [],
    graphData: null,
    statistics: null,
    currentView: 'dashboard',

    init: async function() {
        this.bindEvents();
        await this.loadAllData();
        this.renderView('dashboard');
    },

    bindEvents: function() {
        // Navigation links
        document.querySelectorAll('.nav-item').forEach(item => {
            item.addEventListener('click', (e) => {
                const targetView = item.getAttribute('data-view');
                if (targetView) {
                    this.switchView(targetView);
                }
            });
        });

        // Papers Search & Filter
        const searchInput = document.getElementById('papers-search-input');
        if (searchInput) {
            searchInput.addEventListener('input', () => this.filterAndRenderPapers());
        }

        const topicFilter = document.getElementById('papers-topic-filter');
        if (topicFilter) {
            topicFilter.addEventListener('change', () => this.filterAndRenderPapers());
        }

        const sortSelect = document.getElementById('papers-sort-select');
        if (sortSelect) {
            sortSelect.addEventListener('change', () => this.filterAndRenderPapers());
        }

        const algoSelect = document.getElementById('papers-algo-select');
        if (algoSelect) {
            algoSelect.addEventListener('change', () => this.filterAndRenderPapers());
        }

        // Add Paper Form
        const addPaperForm = document.getElementById('form-add-paper');
        if (addPaperForm) {
            addPaperForm.addEventListener('submit', (e) => this.handleAddPaperSubmit(e));
        }

        // Add Citation Form
        const addCitationForm = document.getElementById('form-add-citation');
        if (addCitationForm) {
            addCitationForm.addEventListener('submit', (e) => this.handleAddCitationSubmit(e));
        }

        // Edit Paper Form
        const editPaperForm = document.getElementById('form-edit-paper');
        if (editPaperForm) {
            editPaperForm.addEventListener('submit', (e) => this.handleEditPaperSubmit(e));
        }
    },

    loadAllData: async function() {
        try {
            await Promise.all([
                this.fetchStatistics(),
                this.fetchPapers(),
                this.fetchGraph()
            ]);
        } catch (err) {
            console.error("Error loading data:", err);
            this.showToast("Failed to connect to backend server.", "error");
        }
    },

    fetchStatistics: async function() {
        const res = await fetch(`${this.apiBase}/api/statistics`);
        this.statistics = await res.json();
        this.renderDashboardStats();
    },

    fetchPapers: async function() {
        const res = await fetch(`${this.apiBase}/api/papers?sort=citations&order=desc&algo=mergesort`);
        const data = await res.json();
        this.allPapers = data.papers || [];
        this.populateTopicDropdowns();
        this.populatePaperSelects();
    },

    fetchGraph: async function() {
        const res = await fetch(`${this.apiBase}/api/graph`);
        this.graphData = await res.json();
        if (window.GraphVisualizer && !window.GraphVisualizer.cy) {
            window.GraphVisualizer.init('cy-container');
        }
        if (window.GraphVisualizer) {
            window.GraphVisualizer.renderGraph(this.graphData, this.allPapers);
        }
    },

    switchView: function(viewName) {
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.toggle('active', item.getAttribute('data-view') === viewName);
        });

        document.querySelectorAll('.view-section').forEach(sec => {
            sec.classList.remove('active');
        });

        const targetSection = document.getElementById(`view-${viewName}`);
        if (targetSection) {
            targetSection.classList.add('active');
        }

        this.currentView = viewName;
        this.renderView(viewName);
    },

    renderView: function(viewName) {
        if (viewName === 'dashboard') {
            this.renderDashboardStats();
        } else if (viewName === 'papers') {
            this.filterAndRenderPapers();
        } else if (viewName === 'graph') {
            if (window.GraphVisualizer) {
                if (!window.GraphVisualizer.cy) {
                    window.GraphVisualizer.init('cy-container');
                }
                window.GraphVisualizer.renderGraph(this.graphData, this.allPapers);
            }
        } else if (viewName === 'rankings') {
            this.loadMaxHeapRankings();
        } else if (viewName === 'trends') {
            if (this.statistics && window.TrendsCharts) {
                window.TrendsCharts.renderCharts(this.statistics);
            }
        } else if (viewName === 'dsa-guide') {
            this.loadDsaGuide();
        }
    },

    renderDashboardStats: function() {
        if (!this.statistics) return;
        const s = this.statistics;

        document.getElementById('stat-total-papers').textContent = s.totalPapers;
        document.getElementById('stat-total-citations').textContent = s.totalCitations;
        document.getElementById('stat-avg-citations').textContent = s.avgCitations;
        document.getElementById('stat-h-index').textContent = s.networkHIndex;
        document.getElementById('stat-density').textContent = s.graphDensity;
        document.getElementById('stat-topics').textContent = s.uniqueTopicsCount;

        if (s.mostCitedPaper && s.mostCitedPaper.title) {
            document.getElementById('dash-top-paper').innerHTML = `
                <strong>[${s.mostCitedPaper.id}] ${s.mostCitedPaper.title}</strong>
                <span class="pill pill-citations" style="margin-left:8px;">${s.mostCitedPaper.inCitationCount} citations</span>
            `;
        }

        if (s.mostReferencingPaper && s.mostReferencingPaper.title) {
            document.getElementById('dash-top-ref').innerHTML = `
                <strong>[${s.mostReferencingPaper.id}] ${s.mostReferencingPaper.title}</strong>
                <span class="pill pill-topic" style="margin-left:8px;">${s.mostReferencingPaper.outCitationCount} references</span>
            `;
        }

        // Render mini top 5 papers on dashboard
        const topTableBody = document.getElementById('dash-top-papers-body');
        if (topTableBody && this.allPapers.length > 0) {
            const top5 = this.allPapers.slice(0, 5);
            topTableBody.innerHTML = top5.map((p, idx) => `
                <tr>
                    <td><strong>#${idx + 1}</strong></td>
                    <td><span class="pill pill-topic">${p.id}</span></td>
                    <td><strong>${this.escapeHtml(p.title)}</strong><br><small style="color:var(--text-muted);">${this.escapeHtml(p.authors)} (${p.year})</small></td>
                    <td><span class="pill pill-citations">${p.inCitationCount} citations</span></td>
                    <td>
                        <button class="btn btn-secondary btn-sm" onclick="App.inspectPaper('${p.id}')">Inspect</button>
                    </td>
                </tr>
            `).join('');
        }
    },

    filterAndRenderPapers: async function() {
        const query = document.getElementById('papers-search-input')?.value || '';
        const topic = document.getElementById('papers-topic-filter')?.value || 'ALL';
        const sortVal = document.getElementById('papers-sort-select')?.value || 'citations-desc';
        const algo = document.getElementById('papers-algo-select')?.value || 'mergesort';

        const [sortBy, order] = sortVal.split('-');

        const url = `${this.apiBase}/api/papers?query=${encodeURIComponent(query)}&topic=${encodeURIComponent(topic)}&sort=${sortBy}&order=${order}&algo=${algo}`;
        const res = await fetch(url);
        const data = await res.json();

        // Update DSA benchmark stats label
        const benchmarkLabel = document.getElementById('sort-benchmark-info');
        if (benchmarkLabel) {
            const ms = (data.executionTimeNanos / 1000000).toFixed(3);
            benchmarkLabel.innerHTML = `Sorted <strong>${data.count} papers</strong> using <strong>${data.algorithm}</strong> in <strong>${ms} ms</strong> (${data.comparisons} comparisons).`;
        }

        const tbody = document.getElementById('papers-table-body');
        if (!tbody) return;

        if (!data.papers || data.papers.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding:30px; color:var(--text-muted);">No research papers matched your search criteria.</td></tr>`;
            return;
        }

        tbody.innerHTML = data.papers.map(p => `
            <tr>
                <td><strong>${p.id}</strong></td>
                <td>
                    <div style="font-weight:600; color:var(--secondary);">${this.escapeHtml(p.title)}</div>
                    <div style="font-size:0.75rem; color:var(--text-muted);">${this.escapeHtml(p.authors)}</div>
                </td>
                <td><span class="pill pill-topic">${this.escapeHtml(p.topic)}</span></td>
                <td><span class="pill pill-year">${p.year}</span></td>
                <td><span class="pill pill-citations">${p.inCitationCount}</span></td>
                <td><span class="pill pill-topic" style="background:#f1f5f9; color:#334155;">${p.outCitationCount}</span></td>
                <td>
                    <div style="display:flex; gap:6px;">
                        <button class="btn btn-secondary btn-sm" onclick="App.inspectPaper('${p.id}')">View</button>
                        <button class="btn btn-secondary btn-sm" onclick="App.openEditModal('${p.id}')">Edit</button>
                        <button class="btn btn-danger btn-sm" onclick="App.deletePaper('${p.id}')">Del</button>
                    </div>
                </td>
            </tr>
        `).join('');
    },

    inspectPaper: async function(paperId) {
        try {
            const res = await fetch(`${this.apiBase}/api/papers/${paperId}`);
            if (!res.ok) {
                this.showToast("Paper not found.", "error");
                return;
            }
            const data = await res.json();
            const p = data.paper;

            document.getElementById('modal-inspect-id').textContent = p.id;
            document.getElementById('modal-inspect-title').textContent = p.title;
            document.getElementById('modal-inspect-authors').textContent = p.authors;
            document.getElementById('modal-inspect-year').textContent = p.year;
            document.getElementById('modal-inspect-topic').textContent = p.topic;
            document.getElementById('modal-inspect-doi').textContent = p.doi;
            document.getElementById('modal-inspect-cites').textContent = p.inCitationCount;
            document.getElementById('modal-inspect-refs').textContent = p.outCitationCount;
            document.getElementById('modal-inspect-abstract').textContent = p.abstractText || "No abstract provided.";

            // References (Out-Neighbors)
            const refsContainer = document.getElementById('modal-inspect-references-list');
            refsContainer.innerHTML = '';
            if (data.references.length === 0) {
                refsContainer.innerHTML = '<span style="color:var(--text-light); font-size:0.8rem;">None</span>';
            } else {
                data.references.forEach(ref => {
                    const btn = document.createElement('button');
                    btn.className = 'pill pill-topic';
                    btn.style.cursor = 'pointer';
                    btn.style.margin = '2px 4px';
                    btn.textContent = `${ref.id} (${ref.title ? ref.title.substring(0, 20) + '...' : ''})`;
                    btn.onclick = () => App.inspectPaper(ref.id);
                    refsContainer.appendChild(btn);
                });
            }

            // Cited By (In-Neighbors)
            const citedByContainer = document.getElementById('modal-inspect-citedby-list');
            citedByContainer.innerHTML = '';
            if (data.citedBy.length === 0) {
                citedByContainer.innerHTML = '<span style="color:var(--text-light); font-size:0.8rem;">None</span>';
            } else {
                data.citedBy.forEach(c => {
                    const btn = document.createElement('button');
                    btn.className = 'pill pill-citations';
                    btn.style.cursor = 'pointer';
                    btn.style.margin = '2px 4px';
                    btn.textContent = `${c.id} (${c.title ? c.title.substring(0, 20) + '...' : ''})`;
                    btn.onclick = () => App.inspectPaper(c.id);
                    citedByContainer.appendChild(btn);
                });
            }

            this.openModal('modal-inspect-paper');

            // Also highlight in cytoscape if on graph view
            if (window.GraphVisualizer) {
                window.GraphVisualizer.focusNode(p.id);
            }
        } catch (err) {
            console.error(err);
            this.showToast("Failed to load paper details.", "error");
        }
    },

    runTraversal: async function(algoType) {
        const startNode = document.getElementById('traversal-start-node')?.value;
        if (!startNode) {
            this.showToast("Please select a valid start paper.", "error");
            return;
        }

        let endpoint = '';
        if (algoType === 'bfs') {
            endpoint = `/api/bfs?start=${startNode}`;
        } else if (algoType === 'dfs') {
            endpoint = `/api/dfs?start=${startNode}`;
        } else if (algoType === 'path') {
            const targetNode = document.getElementById('traversal-target-node')?.value;
            if (!targetNode) {
                this.showToast("Please select a target paper for path finding.", "error");
                return;
            }
            endpoint = `/api/path?source=${startNode}&target=${targetNode}`;
        }

        try {
            const res = await fetch(`${this.apiBase}${endpoint}`);
            const result = await res.json();

            if (window.DsaTracer) {
                window.DsaTracer.loadTraversalResult(result);
            }

            // If Shortest Path, highlight path on Graph
            if (algoType === 'path' && result.path && result.path.length > 0) {
                if (window.GraphVisualizer) {
                    window.GraphVisualizer.highlightPath(result.path);
                }
            }
        } catch (err) {
            console.error(err);
            this.showToast("Traversal execution failed.", "error");
        }
    },

    loadMaxHeapRankings: async function() {
        const limit = document.getElementById('heap-limit-select')?.value || 10;
        try {
            const res = await fetch(`${this.apiBase}/api/rankings?limit=${limit}`);
            const data = await res.json();

            this.renderMaxHeapTree(data.heapStructure);
            this.renderTopRankedTable(data.topPapers);
        } catch (err) {
            console.error(err);
            this.showToast("Failed to compute heap rankings.", "error");
        }
    },

    renderMaxHeapTree: function(heapStructure) {
        const container = document.getElementById('heap-tree-view');
        if (!container || !heapStructure || !heapStructure.heapArray) return;

        const heapArray = heapStructure.heapArray;
        container.innerHTML = '';

        if (heapArray.length === 0) {
            container.innerHTML = '<div style="color:var(--text-light);">Heap is empty.</div>';
            return;
        }

        // Render binary tree level by level
        let index = 0;
        let level = 0;
        while (index < heapArray.length) {
            const levelSize = Math.pow(2, level);
            const levelRow = document.createElement('div');
            levelRow.className = 'heap-level-row';

            for (let i = 0; i < levelSize && index < heapArray.length; i++) {
                const node = heapArray[index];
                const box = document.createElement('div');
                box.className = 'heap-node-box' + (index === 0 ? ' root' : '');
                box.innerHTML = `
                    <div class="h-idx">Index [${node.index}]</div>
                    <div class="h-id">${node.id}</div>
                    <div class="h-cite">${node.citations} cites</div>
                `;
                box.onclick = () => App.inspectPaper(node.id);
                levelRow.appendChild(box);
                index++;
            }
            container.appendChild(levelRow);
            level++;
        }

        // Render Heap Logs
        const logBox = document.getElementById('heap-log-box');
        if (logBox && heapStructure.logs) {
            logBox.innerHTML = heapStructure.logs.map(log => `<div>&bull; ${this.escapeHtml(log)}</div>`).join('');
        }
    },

    renderTopRankedTable: function(topPapers) {
        const tbody = document.getElementById('rankings-table-body');
        if (!tbody || !topPapers) return;

        tbody.innerHTML = topPapers.map((p, idx) => `
            <tr>
                <td><strong>#${idx + 1}</strong></td>
                <td><span class="pill pill-topic">${p.id}</span></td>
                <td><strong>${this.escapeHtml(p.title)}</strong></td>
                <td>${this.escapeHtml(p.authors)}</td>
                <td><span class="pill pill-year">${p.year}</span></td>
                <td><span class="pill pill-citations">${p.inCitationCount} citations</span></td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="App.inspectPaper('${p.id}')">Inspect</button>
                </td>
            </tr>
        `).join('');
    },

    loadDsaGuide: async function() {
        const res = await fetch(`${this.apiBase}/api/dsa-guide`);
        const data = await res.json();

        const structTbody = document.getElementById('dsa-structures-tbody');
        if (structTbody) {
            structTbody.innerHTML = data.structures.map(s => `
                <tr>
                    <td><strong>${s.name}</strong></td>
                    <td>${s.purpose}</td>
                    <td><code>${s.implementation}</code></td>
                    <td><span class="pill pill-topic">${s.time}</span></td>
                    <td><span class="pill pill-year">${s.space}</span></td>
                </tr>
            `).join('');
        }

        const vivaContainer = document.getElementById('dsa-viva-cards');
        if (vivaContainer) {
            vivaContainer.innerHTML = data.vivaScenarios.map(v => `
                <div class="panel" style="margin-bottom:16px;">
                    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
                        <h4 style="color:var(--primary); font-size:1rem;">📌 ${v.problem}</h4>
                        <span class="pill pill-citations">${v.complexity}</span>
                    </div>
                    <div style="font-size:0.85rem; line-height:1.6;">
                        <strong>DSA Mapping:</strong> Problem &rarr; <code>${v.dataStructure}</code> &rarr; <code>${v.algorithm}</code> &rarr; <code>${v.output}</code>
                    </div>
                </div>
            `).join('');
        }
    },

    // Form Handlers
    handleAddPaperSubmit: async function(e) {
        e.preventDefault();
        const paper = {
            id: document.getElementById('add-paper-id').value.trim(),
            title: document.getElementById('add-paper-title').value.trim(),
            authors: document.getElementById('add-paper-authors').value.trim(),
            year: parseInt(document.getElementById('add-paper-year').value.trim(), 10),
            topic: document.getElementById('add-paper-topic').value.trim(),
            doi: document.getElementById('add-paper-doi').value.trim(),
            abstractText: document.getElementById('add-paper-abstract').value.trim()
        };

        try {
            const res = await fetch(`${this.apiBase}/api/papers`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(paper)
            });
            const data = await res.json();

            if (!res.ok) {
                this.showToast(data.error || "Failed to add paper.", "error");
                return;
            }

            this.showToast("Paper created successfully!", "success");
            this.closeModal('modal-add-paper');
            document.getElementById('form-add-paper').reset();
            await this.loadAllData();
            this.filterAndRenderPapers();
        } catch (err) {
            console.error(err);
            this.showToast("Error creating paper.", "error");
        }
    },

    handleAddCitationSubmit: async function(e) {
        e.preventDefault();
        const source = document.getElementById('cite-source-select').value;
        const target = document.getElementById('cite-target-select').value;

        if (!source || !target) {
            this.showToast("Please choose both source and target papers.", "error");
            return;
        }

        try {
            const res = await fetch(`${this.apiBase}/api/citations`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ source, target })
            });
            const data = await res.json();

            if (!res.ok) {
                this.showToast(data.error || "Failed to add citation.", "error");
                return;
            }

            this.showToast(`Citation edge added: ${source} → ${target}`, "success");
            this.closeModal('modal-add-citation');
            await this.loadAllData();
        } catch (err) {
            console.error(err);
            this.showToast("Error adding citation edge.", "error");
        }
    },

    openEditModal: async function(paperId) {
        const p = this.allPapers.find(item => item.id === paperId);
        if (!p) return;

        document.getElementById('edit-paper-id').value = p.id;
        document.getElementById('edit-paper-title').value = p.title;
        document.getElementById('edit-paper-authors').value = p.authors;
        document.getElementById('edit-paper-year').value = p.year;
        document.getElementById('edit-paper-topic').value = p.topic;
        document.getElementById('edit-paper-doi').value = p.doi;
        document.getElementById('edit-paper-abstract').value = p.abstractText;

        this.openModal('modal-edit-paper');
    },

    handleEditPaperSubmit: async function(e) {
        e.preventDefault();
        const id = document.getElementById('edit-paper-id').value;
        const updated = {
            title: document.getElementById('edit-paper-title').value.trim(),
            authors: document.getElementById('edit-paper-authors').value.trim(),
            year: parseInt(document.getElementById('edit-paper-year').value.trim(), 10),
            topic: document.getElementById('edit-paper-topic').value.trim(),
            doi: document.getElementById('edit-paper-doi').value.trim(),
            abstractText: document.getElementById('edit-paper-abstract').value.trim()
        };

        try {
            const res = await fetch(`${this.apiBase}/api/papers/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(updated)
            });
            const data = await res.json();

            if (!res.ok) {
                this.showToast(data.error || "Failed to update paper.", "error");
                return;
            }

            this.showToast("Paper updated successfully!", "success");
            this.closeModal('modal-edit-paper');
            await this.loadAllData();
            this.filterAndRenderPapers();
        } catch (err) {
            console.error(err);
            this.showToast("Error updating paper.", "error");
        }
    },

    deletePaper: async function(paperId) {
        if (!confirm(`Are you sure you want to delete paper ${paperId}? This will cascade delete all its citation edges in the graph.`)) {
            return;
        }

        try {
            const res = await fetch(`${this.apiBase}/api/papers/${paperId}`, {
                method: 'DELETE'
            });
            const data = await res.json();

            if (!res.ok) {
                this.showToast(data.error || "Failed to delete paper.", "error");
                return;
            }

            this.showToast(`Paper ${paperId} deleted.`, "info");
            await this.loadAllData();
            this.filterAndRenderPapers();
        } catch (err) {
            console.error(err);
            this.showToast("Error deleting paper.", "error");
        }
    },

    resetDataset: async function() {
        if (!confirm("Reset database back to default 55 research papers?")) return;
        try {
            const res = await fetch(`${this.apiBase}/api/reset`, { method: 'POST' });
            const data = await res.json();
            this.showToast(data.message || "Dataset reset.", "success");
            await this.loadAllData();
            this.renderView(this.currentView);
        } catch (err) {
            console.error(err);
            this.showToast("Failed to reset dataset.", "error");
        }
    },

    // UI Helpers
    populateTopicDropdowns: function() {
        const topics = Array.from(new Set(this.allPapers.map(p => p.topic))).sort();
        const filterSelect = document.getElementById('papers-topic-filter');
        if (filterSelect) {
            filterSelect.innerHTML = `<option value="ALL">All Topics (${topics.length})</option>` +
                topics.map(t => `<option value="${t}">${t}</option>`).join('');
        }
    },

    populatePaperSelects: function() {
        const sorted = [...this.allPapers].sort((a, b) => a.id.localeCompare(b.id));
        const options = sorted.map(p => `<option value="${p.id}">[${p.id}] ${this.escapeHtml(p.title.substring(0, 38))}...</option>`).join('');

        const startSelect = document.getElementById('traversal-start-node');
        if (startSelect) startSelect.innerHTML = options;

        const targetSelect = document.getElementById('traversal-target-node');
        if (targetSelect) targetSelect.innerHTML = options;

        const citeSource = document.getElementById('cite-source-select');
        if (citeSource) citeSource.innerHTML = '<option value="">-- Choose Source Paper (citing) --</option>' + options;

        const citeTarget = document.getElementById('cite-target-select');
        if (citeTarget) citeTarget.innerHTML = '<option value="">-- Choose Target Paper (cited) --</option>' + options;
    },

    openModal: function(modalId) {
        const el = document.getElementById(modalId);
        if (el) el.classList.add('open');
    },

    closeModal: function(modalId) {
        const el = document.getElementById(modalId);
        if (el) el.classList.remove('open');
    },

    showToast: function(message, type = 'info') {
        const container = document.getElementById('toast-container');
        if (!container) return;

        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.innerHTML = `<span>${message}</span>`;
        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 300);
        }, 3500);
    },

    escapeHtml: function(str) {
        if (!str) return '';
        return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
    }
};

window.App = App;

// Bootstrap on DOM Ready
document.addEventListener('DOMContentLoaded', () => {
    App.init();
});
