export default function EmptyState({
  title = "Nothing here yet",
  message = "There is no data to display."
}) {
  return (
    <div className="empty-state">
      <div className="empty-icon">○</div>
      <h3>{title}</h3>
      <p>{message}</p>
    </div>
  );
}
