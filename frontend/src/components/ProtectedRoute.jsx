import { Navigate } from "react-router-dom";

// bu component sayesinde login olmayan kullanici dashboard'a giremez

function ProtectedRoute({ children }) {
  const token = localStorage.getItem("token");

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

export default ProtectedRoute;