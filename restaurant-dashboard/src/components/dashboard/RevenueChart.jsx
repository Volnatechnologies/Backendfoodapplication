export default function RevenueChart() {
  const points = "0,82 18,68 36,73 54,45 72,54 90,32 108,42 126,18 144,26 162,12";
  return (
    <div className="revenue-chart">
      <div className="chart-grid">
        <span>₹5L</span>
        <span>₹4L</span>
        <span>₹3L</span>
        <span>₹2L</span>
        <span>₹1L</span>
      </div>
      <svg viewBox="0 0 162 90" preserveAspectRatio="none" className="chart-svg">
        <polyline points={points} fill="none" stroke="currentColor" strokeWidth="2.5" />
        <polyline
          points={`${points} 162,90 0,90`}
          fill="currentColor"
          opacity="0.08"
          stroke="none"
        />
      </svg>
      <div className="chart-labels">
        <span>Mon</span><span>Tue</span><span>Wed</span><span>Thu</span>
        <span>Fri</span><span>Sat</span><span>Sun</span>
      </div>
    </div>
  );
}
