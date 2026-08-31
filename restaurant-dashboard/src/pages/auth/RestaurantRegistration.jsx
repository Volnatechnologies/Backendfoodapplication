import { Link } from "react-router-dom";

export default function RestaurantRegistration() {
  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>Restaurant Registration</h1>
        <p>Your completed restaurant registration flow can be mounted here.</p>
        <Link className="app-button app-button-primary" to="/dashboard">
          Go to Dashboard
        </Link>
      </div>
    </div>
  );
}
