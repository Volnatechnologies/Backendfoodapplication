import { Link } from "react-router-dom";

export default function Signup() {
  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1>Restaurant Sign Up</h1>
        <p>Your existing signup screen remains here.</p>
        <Link className="app-button app-button-primary" to="/restaurant-registration">
          Continue to Restaurant Registration
        </Link>
        <p className="auth-footer">
          Already registered? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  );
}
