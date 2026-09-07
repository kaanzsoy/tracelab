import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import api from "../api/api";

function TestRunsPage() {
  const navigate = useNavigate();
  const { projectId } = useParams();

  const [project, setProject] = useState(null);
  const [testRuns, setTestRuns] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const user = JSON.parse(
    localStorage.getItem("user") || "{}"
  );

  const canEdit =
    user.role === "ADMIN" ||
    user.role === "TESTER";

  const loadData = async () => {
    try {
      setError("");

      const [
        projectResponse,
        testRunsResponse,
      ] = await Promise.all([
        api.get(`/projects/${projectId}`),
        api.get(
          `/projects/${projectId}/test-runs`
        ),
      ]);

      setProject(projectResponse.data);
      setTestRuns(testRunsResponse.data);
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Test runs could not be loaded."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [projectId]);

  const handleDelete = async (testRunId) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this test run?"
    );

    if (!confirmed) {
      return;
    }

    try {
      await api.delete(
        `/test-runs/${testRunId}`
      );

      setTestRuns((current) =>
        current.filter(
          (testRun) =>
            testRun.id !== testRunId
        )
      );
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Test run could not be deleted."
      );
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        Loading test runs...
      </div>
    );
  }

  return (
    <div className="page-container">
      <header className="top-bar">
        <div>
          <h1>Test Runs</h1>

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
          <h2>Project Test Runs</h2>

          <p>
            Plan and track test execution cycles.
          </p>
        </div>

        {canEdit && (
          <button
            className="primary-button"
            onClick={() =>
              navigate(
                `/projects/${projectId}/test-runs/new`
              )
            }
          >
            New Test Run
          </button>
        )}
      </section>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {testRuns.length === 0 ? (
        <div className="empty-state">
          No test runs found for this project.
        </div>
      ) : (
        <div className="test-run-list">
          {testRuns.map((testRun) => (
            <div
              key={testRun.id}
              className="test-run-card"
            >
              <div className="test-run-card-header">
                <div>
                  <span className="code-label">
                    {testRun.runCode}
                  </span>

                  <h3>{testRun.name}</h3>
                </div>

                <div className="badge-group">
                  <span className="environment-badge">
                    {testRun.environment}
                  </span>

                  <span
                    className={`status-badge status-${testRun.status?.toLowerCase()}`}
                  >
                    {testRun.status}
                  </span>
                </div>
              </div>

              <p>
                {testRun.description ||
                  "No description provided."}
              </p>

              <div className="test-run-details">
                <div>
                  <strong>Started At</strong>

                  <span>
                    {formatDate(
                      testRun.startedAt
                    )}
                  </span>
                </div>

                <div>
                  <strong>Completed At</strong>

                  <span>
                    {formatDate(
                      testRun.completedAt
                    )}
                  </span>
                </div>
              </div>

              <div className="card-actions">
                <button
                  className="secondary-button"
                  onClick={() =>
                    navigate(
                      `/test-runs/${testRun.id}/executions`
                    )
                  }
                >
                  Executions
                </button>

                {canEdit && (
                  <>
                    <button
                      className="secondary-button"
                      onClick={() =>
                        navigate(
                          `/projects/${projectId}/test-runs/${testRun.id}/edit`
                        )
                      }
                    >
                      Edit
                    </button>

                    <button
                      className="danger-button"
                      onClick={() =>
                        handleDelete(
                          testRun.id
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

function formatDate(value) {
  if (!value) {
    return "Not set";
  }

  return new Date(value).toLocaleString();
}

export default TestRunsPage;