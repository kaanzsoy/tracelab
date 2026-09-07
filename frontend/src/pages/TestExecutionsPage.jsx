import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import api from "../api/api";

function TestExecutionsPage() {
  const navigate = useNavigate();
  const { testRunId } = useParams();

  const [testRun, setTestRun] = useState(null);
  const [executions, setExecutions] = useState([]);

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
        testRunResponse,
        executionsResponse,
      ] = await Promise.all([
        api.get(`/test-runs/${testRunId}`),
        api.get(`/test-runs/${testRunId}/executions`),
      ]);

      setTestRun(testRunResponse.data);
      setExecutions(executionsResponse.data);
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Test executions could not be loaded."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [testRunId]);

  const handleDelete = async (executionId) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this execution?"
    );

    if (!confirmed) {
      return;
    }

    try {
      await api.delete(
        `/test-executions/${executionId}`
      );

      setExecutions((current) =>
        current.filter(
          (execution) =>
            execution.id !== executionId
        )
      );
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Execution could not be deleted."
      );
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        Loading executions...
      </div>
    );
  }

  return (
    <div className="page-container">
      <header className="top-bar">
        <div>
          <h1>Test Executions</h1>

          <p>
            {testRun?.runCode} · {testRun?.name}
          </p>
        </div>

        <div className="header-actions">
          <button
            className="secondary-button"
            onClick={() =>
              navigate(
                `/projects/${testRun?.projectId}/test-runs`
              )
            }
          >
            Test Runs
          </button>

          <button
            className="secondary-button"
            onClick={() =>
              navigate(
                `/projects/${testRun?.projectId}/dashboard`
              )
            }
          >
            Dashboard
          </button>
        </div>
      </header>

      <section className="section-header">
        <div>
          <h2>Run Executions</h2>

          <p>
            Add test cases to this run and record their
            execution results.
          </p>
        </div>

        {canEdit &&
          !isRunLocked(testRun?.status) && (
            <button
              className="primary-button"
              onClick={() =>
                navigate(
                  `/test-runs/${testRunId}/executions/new`
                )
              }
            >
              Add Test Case
            </button>
          )}
      </section>

      {isRunLocked(testRun?.status) && (
        <div className="info-message">
          This test run is {testRun.status}. Its
          executions can no longer be modified.
        </div>
      )}

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {executions.length === 0 ? (
        <div className="empty-state">
          No test executions found for this run.
        </div>
      ) : (
        <div className="execution-list">
          {executions.map((execution) => (
            <div
              key={execution.id}
              className="execution-card"
            >
              <div className="execution-card-header">
                <div>
                  <span className="code-label">
                    {execution.executionCode}
                  </span>

                  <h3>
                    {execution.testCaseCode} ·{" "}
                    {execution.testCaseTitle}
                  </h3>
                </div>

                <span
                  className={`result-badge result-${execution.result?.toLowerCase()}`}
                >
                  {execution.result}
                </span>
              </div>

              <div className="execution-details">
                <DetailBlock
                  title="Actual Result"
                  value={execution.actualResult}
                />

                <DetailBlock
                  title="Notes"
                  value={execution.notes}
                />

                <DetailBlock
                  title="Executed At"
                  value={formatDate(
                    execution.executedAt
                  )}
                />
              </div>

              <div className="card-actions">
                {execution.result === "FAILED" && (
                  <button
                    className="secondary-button"
                    onClick={() =>
                      navigate(
                        `/test-executions/${execution.id}/defects/new`
                      )
                    }
                  >
                    Create Defect
                  </button>
                )}

                {canEdit &&
                  !isRunLocked(testRun?.status) && (
                    <>
                      <button
                        className="secondary-button"
                        onClick={() =>
                          navigate(
                            `/test-runs/${testRunId}/executions/${execution.id}/edit`
                          )
                        }
                      >
                        Edit Result
                      </button>

                      <button
                        className="danger-button"
                        onClick={() =>
                          handleDelete(
                            execution.id
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

function isRunLocked(status) {
  return (
    status === "COMPLETED" ||
    status === "CANCELLED"
  );
}

function formatDate(value) {
  if (!value) {
    return "Not set";
  }

  return new Date(value).toLocaleString();
}

function DetailBlock({ title, value }) {
  return (
    <div className="detail-block">
      <strong>{title}</strong>

      <p>{value || "Not specified."}</p>
    </div>
  );
}

export default TestExecutionsPage;