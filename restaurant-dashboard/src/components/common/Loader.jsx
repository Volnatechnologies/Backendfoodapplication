export default function Loader({ text = "Loading dashboard..." }) {
  return (
    <div className="loader-wrap">
      <div className="loader-spinner" />
      <span>{text}</span>
    </div>
  );
}
