import useAuth from "../../hooks/useAuth";

export default function Profile() {
  const { user } = useAuth();

  return (
    <div className="simple-page">
      <span className="eyebrow">BUSINESS ACCOUNT</span>
      <h1>Profile</h1>
      <div className="dashboard-card page-card">
        <h2>Restaurant Owner</h2>
        <p>{user?.email || "Authenticated restaurant owner"}</p>
      </div>
    </div>
  );
}
