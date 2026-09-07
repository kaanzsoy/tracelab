import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";

function RequirementsPage() {
  const navigate = useNavigate();
  const { projectId } = useParams();

  const [requirements, setRequirements] = useState([]);
  const [project, setProject] = useState(null);

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const user = JSON.parse(
    localStorage.getItem("user") || "{}"
  );

  const canEdit =
    user.role === "ADMIN" ||
    user.role === "TESTER";

  const loadData = async () => {
    try {
      setError("");

      // proje adi ve requirement listesi ayni anda geliyor
      const [
        projectResponse,
        requirementsResponse,
      ] = await Promise.all([
        api.get(`/projects/${projectId}`),
        api.get(
          `/projects/${projectId}/requirements`
        ),
      ]);

      setProject(projectResponse.data);
      setRequirements(requirementsResponse.data);
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Requirements could not be loaded."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [projectId]);

  const handleDelete = async (requirementId) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this requirement?"
    );

    if (!confirmed) {
      return;
    }

    try {
      await api.delete(
        `/requirements/${requirementId}`
      );

      setRequirements((current) =>
        current.filter(
          (requirement) =>
            requirement.id !== requirementId
        )
      );
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Requirement could not be deleted."
      );
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        Loading requirements...
      </div>
    );
  }

  return (
    <div className="page-container">
      <header className="top-bar">
        <div>
          <h1>Requirements</h1>

          <p>
            {project?.name || "TraceLab Project"}
          </p>
        </div>

        <div className="header-actions">
          <button
            className="secondary-button"
            onClick={() =>
              navigate(
                `/projects/${projectId}/dashboard`
              )
            }
          >
            Dashboard
          </button>

          <button
            className="secondary-button"
            onClick={() =>
              navigate("/projects")
            }
          >
            Projects
          </button>
        </div>
      </header>

      <section className="section-header">
        <div>
          <h2>Project Requirements</h2>

          <p>
            Manage requirements and their linked
            test cases.
          </p>
        </div>

        {canEdit && (
          <button
            className="primary-button"
            onClick={() =>
              navigate(
                `/projects/${projectId}/requirements/new`
              )
            }
          >
            New Requirement
          </button>
        )}
      </section>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {requirements.length === 0 ? (
        <div className="empty-state">
          No requirements found for this project.
        </div>
      ) : (
        <div className="requirements-list">
          {requirements.map((requirement) => (
            <div
              key={requirement.id}
              className="requirement-card"
            >
              <div className="requirement-card-header">
                <div>
                  <span className="code-label">
                    {requirement.requirementCode}
                  </span>

                  <h3>
                    {requirement.title}
                  </h3>
                </div>

                <div className="badge-group">
                  <span
                    className={`priority-badge priority-${requirement.priority?.toLowerCase()}`}
                  >
                    {requirement.priority}
                  </span>

                  <span
                    className={`status-badge status-${requirement.status?.toLowerCase()}`}
                  >
                    {requirement.status}
                  </span>
                </div>
              </div>

              <p>
                {requirement.description ||
                  "No description provided."}
              </p>

              <div className="card-actions">
                <button
                  className="secondary-button"
                  onClick={() =>
                    navigate(
                      `/requirements/${requirement.id}/test-cases`
                    )
                  }
                >
                  Test Cases
                </button>

                {canEdit && (
                  <>
                    <button
                      className="secondary-button"
                      onClick={() =>
                        navigate(
                          `/requirements/${requirement.id}/edit`
                        )
                      }
                    >
                      Edit
                    </button>

                    <button
                      className="danger-button"
                      onClick={() =>
                        handleDelete(
                          requirement.id
                        )
                      }
                    >
                      Delete
                    </button>
                  </>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default RequirementsPage;