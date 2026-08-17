/**
 * CitationFlow: Interactive Step-by-Step Traversal & DSA Algorithm Tracer
 */

const DsaTracer = {
    currentResult: null,
    currentStepIndex: -1,
    isPlaying: false,
    timer: null,
    speedMs: 800,

    loadTraversalResult: function(result) {
        this.stop();
        this.currentResult = result;
        this.currentStepIndex = -1;

        // Render meta summary
        document.getElementById('tracer-algo-name').textContent = result.algorithm || 'Graph Traversal';
        document.getElementById('tracer-time-comp').textContent = result.timeComplexity || 'O(V + E)';
        document.getElementById('tracer-space-comp').textContent = result.spaceComplexity || 'O(V)';
        document.getElementById('tracer-time-taken').textContent = (result.executionTimeNanos / 1000000).toFixed(3) + ' ms';
        document.getElementById('tracer-explanation').textContent = result.explanation || '';

        // Cycle banner
        const cycleBanner = document.getElementById('tracer-cycle-alert');
        if (cycleBanner) {
            if (result.cycleDetected) {
                cycleBanner.style.display = 'block';
                cycleBanner.innerHTML = `⚠️ <strong>Citation Cycle Detected!</strong> Lineage loop: ${result.cyclePath.join(' &rarr; ')}`;
            } else {
                cycleBanner.style.display = 'none';
            }
        }

        // Render step list in tracker box
        const stepContainer = document.getElementById('step-tracker-list');
        stepContainer.innerHTML = '';

        if (!result.steps || result.steps.length === 0) {
            stepContainer.innerHTML = '<div class="step-item">No traversal steps generated.</div>';
            return;
        }

        result.steps.forEach((step, idx) => {
            const el = document.createElement('div');
            el.className = 'step-item';
            el.id = `step-log-${idx}`;
            el.innerHTML = `
                <div style="display:flex; justify-content:space-between; margin-bottom:4px;">
                    <strong>Step ${step.stepNumber}: ${step.action}</strong>
                    <span style="color:#94a3b8;">${step.currentNode || ''}</span>
                </div>
                <div style="font-size:0.75rem; color:#cbd5e1;">${step.message}</div>
            `;
            el.addEventListener('click', () => this.jumpToStep(idx));
            stepContainer.appendChild(el);
        });

        this.updateControls();
    },

    play: function() {
        if (!this.currentResult || !this.currentResult.steps) return;
        this.isPlaying = true;
        document.getElementById('btn-tracer-play').innerHTML = '⏸ Pause';

        this.timer = setInterval(() => {
            if (this.currentStepIndex < this.currentResult.steps.length - 1) {
                this.stepForward();
            } else {
                this.stop();
            }
        }, this.speedMs);
    },

    pause: function() {
        this.isPlaying = false;
        clearInterval(this.timer);
        const playBtn = document.getElementById('btn-tracer-play');
        if (playBtn) playBtn.innerHTML = '▶ Play Trace';
    },

    stop: function() {
        this.pause();
    },

    togglePlay: function() {
        if (this.isPlaying) {
            this.pause();
        } else {
            this.play();
        }
    },

    stepForward: function() {
        if (!this.currentResult || !this.currentResult.steps) return;
        if (this.currentStepIndex < this.currentResult.steps.length - 1) {
            this.jumpToStep(this.currentStepIndex + 1);
        }
    },

    stepBackward: function() {
        if (!this.currentResult || !this.currentResult.steps) return;
        if (this.currentStepIndex > 0) {
            this.jumpToStep(this.currentStepIndex - 1);
        }
    },

    reset: function() {
        this.stop();
        if (this.currentResult && this.currentResult.steps && this.currentResult.steps.length > 0) {
            this.jumpToStep(0);
        } else {
            this.currentStepIndex = -1;
            this.updateControls();
        }
        if (window.GraphVisualizer) {
            window.GraphVisualizer.clearHighlights();
        }
    },

    jumpToStep: function(index) {
        if (!this.currentResult || !this.currentResult.steps || index < 0 || index >= this.currentResult.steps.length) return;

        // Remove active class from previous step log
        if (this.currentStepIndex >= 0) {
            const prevEl = document.getElementById(`step-log-${this.currentStepIndex}`);
            if (prevEl) prevEl.classList.remove('active-step');
        }

        this.currentStepIndex = index;
        const currentStep = this.currentResult.steps[index];

        // Highlight current step log item and scroll into view
        const currentEl = document.getElementById(`step-log-${index}`);
        if (currentEl) {
            currentEl.classList.add('active-step');
            currentEl.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
        }

        // Render Data Structure State (Queue / Stack)
        const dsContainer = document.getElementById('ds-state-pills');
        if (dsContainer) {
            dsContainer.innerHTML = '';
            if (currentStep.dataStructureState.length === 0) {
                dsContainer.innerHTML = '<span style="color:var(--text-light); font-size:0.8rem;">[ Empty ]</span>';
            } else {
                currentStep.dataStructureState.forEach((nodeId, idx) => {
                    const pill = document.createElement('span');
                    pill.className = 'ds-node-pill' + (nodeId === currentStep.currentNode ? ' active' : '');
                    pill.textContent = nodeId;
                    dsContainer.appendChild(pill);
                });
            }
        }

        // Render Visited Set
        const visitedContainer = document.getElementById('visited-state-pills');
        if (visitedContainer) {
            visitedContainer.innerHTML = '';
            currentStep.visitedSet.forEach(nodeId => {
                const pill = document.createElement('span');
                pill.className = 'ds-node-pill';
                pill.style.background = '#0284c7';
                pill.textContent = nodeId;
                visitedContainer.appendChild(pill);
            });
        }

        // Update step counter label
        const counterLabel = document.getElementById('tracer-step-counter');
        if (counterLabel) {
            counterLabel.textContent = `Step ${index + 1} of ${this.currentResult.steps.length}`;
        }

        // Highlight active node in Cytoscape
        if (window.GraphVisualizer) {
            window.GraphVisualizer.highlightActiveTraceNode(currentStep.currentNode || currentStep.neighborNode);
        }

        this.updateControls();
    },

    setSpeed: function(speedVal) {
        this.speedMs = parseInt(speedVal, 10) || 800;
        if (this.isPlaying) {
            this.pause();
            this.play();
        }
    },

    updateControls: function() {
        const total = this.currentResult && this.currentResult.steps ? this.currentResult.steps.length : 0;
        const prevBtn = document.getElementById('btn-tracer-prev');
        const nextBtn = document.getElementById('btn-tracer-next');

        if (prevBtn) prevBtn.disabled = this.currentStepIndex <= 0;
        if (nextBtn) nextBtn.disabled = this.currentStepIndex >= total - 1;
    }
};

window.DsaTracer = DsaTracer;
