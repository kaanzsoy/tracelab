import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import api from "../api/api";

function DefectsPage() {
  const navigate = useNavigate();
  const { projectId } = useParams();

  const [project, setProject] = useState(null);
  const [defects, setDefects] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const user = JSON.parse(
    localStorage.getItem("user") || "{}"
  );

  const canUpdate =
    user.role === "ADMIN" ||
    user.role === "TESTER" ||
    user.role === "DEVELOPER";

  const canDelete =
    user.role === "ADMIN";

  const loadData = async () => {
    try {
      setError("");

      const [
        projectResponse,
        defectsResponse,
      ] = await Promise.all([
        api.get(`/projects/${projectId}`),
        api.get(
          `/projects/${projectId}/defects`
        ),
      ]);

      setProject(projectResponse.data);
      setDefects(defectsResponse.data);
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Defects could not be loaded."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [projectId]);

  const handleDelete = async (defectId) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this defect?"
    );

    if (!confirmed) {
      return;
    }

    try {
      await api.delete(
        `/defects/${defectId}`
      );

      setDefects((current) =>
        current.filter(
          (defect) =>
            defect.id !== defectId
        )
      );
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Defect could not be deleted."
      );
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        Loading defects...
      </div>
    );
  }

  return (
    <div className="page-container">
      <header className="top-bar">
        <div>
          <h1>Defects</h1>

          <p>
            {project?.name ||
              "TraceLab Project"}
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
          <h2>Project Defects</h2>

          <p>
            Track defects discovered during test
            execution.
          </p>
        </div>
      </section>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {defects.length === 0 ? (
        <div className="empty-state">
          No defects found for this project.
        </div>
      ) : (
        <div className="defect-list">
          {defects.map((defect) => (
            <div
              key={defect.id}
              className="defect-card"
            >
              <div className="defect-card-header">
                <div>
                  <span className="code-label">
                    {defect.defectCode}
                  </span>

                  <h3>{defect.title}</h3>
                </div>

                <div className="badge-group">
                  <span
                    className={`severity-badge severity-${defect.severity?.toLowerCase()}`}
                  >
                    {defect.severity}
                  </span>

                  <span
                    className={`status-badge status-${defect.status?.toLowerCase()}`}
                  >
                    {defect.status}
                  </span>
                </div>
              </div>

              <p>
                {defect.description ||
                  "No description provided."}
              </p>

              <div className="defect-details">
                <DetailBlock
                  title="Assigned To"
                  value={defect.assignedTo}
                />

                <DetailBlock
                  title="Resolution Notes"
                  value={defect.resolutionNotes}
                />

                <DetailBlock
                  title="Execution"
                  value={
                    defect.executionCode ||
                    defect.testExecutionCode ||
                    `ID ${defect.testExecutionId ?? ""}`
                  }
                />
              </div>

              <div className="card-actions">
                {canUpdate && (
                  <button
                    className="secondary-button"
                    onClick={() =>
                      navigate(
                        `/projects/${projectId}/defects/${defect.id}/edit`
                      )
                    }
                  >
                    Edit
                  </button>
                )}

                {canDelete && (
                  <button
                    className="danger-button"
                    onClick={() =>
                      handleDelete(defect.id)
                    }
                  >
                    Delete
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

function DetailBlock({ title, value }) {
  return (
    <div className="detail-block">
      <strong>{title}</strong>
      <p>{value || "Not specified."}</p>
    </div>
  );
}

export default DefectsPage;