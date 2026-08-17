/**
 * CitationFlow: Research Trends & Analytics Charts
 * Powered by Chart.js
 */

const TrendsCharts = {
    topicChart: null,
    yearChart: null,

    renderCharts: function(statsData) {
        if (typeof Chart === 'undefined') return;

        this.renderTopicChart(statsData.topicPaperCounts, statsData.topicCitationCounts);
        this.renderYearChart(statsData.yearPaperCounts);
    },

    renderTopicChart: function(paperCounts, citationCounts) {
        const ctx = document.getElementById('chart-topic-distribution');
        if (!ctx) return;

        if (this.topicChart) {
            this.topicChart.destroy();
        }

        const labels = Object.keys(paperCounts || {});
        const pCounts = Object.values(paperCounts || {});
        const cCounts = Object.values(citationCounts || {});

        const bgColors = [
            '#3b82f6', '#8b5cf6', '#ec4899', '#10b981',
            '#f59e0b', '#ef4444', '#06b6d4', '#6366f1', '#14b8a6'
        ];

        this.topicChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [
                    {
                        label: 'Paper Count',
                        data: pCounts,
                        backgroundColor: '#3b82f6',
                        borderRadius: 6
                    },
                    {
                        label: 'Total Citations',
                        data: cCounts,
                        backgroundColor: '#f59e0b',
                        borderRadius: 6
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { position: 'top' },
                    tooltip: { mode: 'index', intersect: false }
                },
                scales: {
                    x: { grid: { display: false } },
                    y: { beginAtZero: true, grid: { color: '#f1f5f9' } }
                }
            }
        });
    },

    renderYearChart: function(yearCounts) {
        const ctx = document.getElementById('chart-year-distribution');
        if (!ctx) return;

        if (this.yearChart) {
            this.yearChart.destroy();
        }

        const labels = Object.keys(yearCounts || {});
        const data = Object.values(yearCounts || {});

        this.yearChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Papers Published',
                    data: data,
                    borderColor: '#10b981',
                    backgroundColor: 'rgba(16, 185, 129, 0.15)',
                    tension: 0.35,
                    fill: true,
                    pointBackgroundColor: '#10b981',
                    pointRadius: 5
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    x: { grid: { display: false } },
                    y: { beginAtZero: true, ticks: { stepSize: 1 } }
                }
            }
        });
    }
};

window.TrendsCharts = TrendsCharts;
