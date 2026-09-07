import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";

function ProjectsPage() {
  const navigate = useNavigate();

  const [projects, setProjects] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const user = JSON.parse(
    localStorage.getItem("user") || "{}"
  );

  const loadProjects = async () => {
    try {
      setError("");

      // frontend'e gercek listeleme yapilir
      const response = await api.get("/projects");

      setProjects(response.data);
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Projects could not be loaded."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProjects();
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");

    navigate("/login");
  };

  if (loading) {
    return (
      <div className="page-container">
        Loading projects...
      </div>
    );
  }

  return (
    <div className="page-container">
      <header className="top-bar">
        <div>
          <h1>TraceLab</h1>

          <p>
            Welcome, {user.fullName || user.username}
            {user.role && ` · ${user.role}`}
          </p>
        </div>

        <button
          className="secondary-button"
          onClick={handleLogout}
        >
          Logout
        </button>
      </header>

      <section className="section-header">
        <div>
          <h2>Projects</h2>
          <p>Select a project to view its dashboard.</p>
        </div>

        {user.role === "ADMIN" && (
          <button
            className="primary-button"
            onClick={() =>
              navigate("/projects/new")
            }
          >
            New Project
          </button>
        )}
      </section>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {projects.length === 0 ? (
        <div className="empty-state">
          No projects found.
        </div>
      ) : (
        <div className="projects-grid">
          {projects.map((project) => (
            <button
              key={project.id}
              className="project-card"
              onClick={() =>
                navigate(
                  `/projects/${project.id}/dashboard`
                )
              }
            >
              <div className="project-card-top">
                <h3>{project.name}</h3>

                <span
                  className={`status-badge status-${project.status?.toLowerCase()}`}
                >
                  {project.status}
                </span>
              </div>

              <p>
                {project.description ||
                  "No description provided."}
              </p>

              <small>
                Project ID: {project.id}
              </small>
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

export default ProjectsPage;