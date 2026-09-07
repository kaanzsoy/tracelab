import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import api from "../api/api";

function TestCasesPage() {
  const navigate = useNavigate();
  const { requirementId } = useParams();

  const [requirement, setRequirement] = useState(null);
  const [testCases, setTestCases] = useState([]);

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
        requirementResponse,
        testCasesResponse,
      ] = await Promise.all([
        api.get(`/requirements/${requirementId}`),
        api.get(
          `/requirements/${requirementId}/test-cases`
        ),
      ]);

      setRequirement(requirementResponse.data);
      setTestCases(testCasesResponse.data);
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Test cases could not be loaded."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [requirementId]);

  const handleDelete = async (testCaseId) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this test case?"
    );

    if (!confirmed) {
      return;
    }

    try {
      await api.delete(
        `/test-cases/${testCaseId}`
      );

      setTestCases((current) =>
        current.filter(
          (testCase) =>
            testCase.id !== testCaseId
        )
      );
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Test case could not be deleted."
      );
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        Loading test cases...
      </div>
    );
  }

  return (
    <div className="page-container">
      <header className="top-bar">
        <div>
          <h1>Test Cases</h1>

          <p>
            {requirement?.requirementCode} ·{" "}
            {requirement?.title}
          </p>
        </div>

        <div className="header-actions">
          <button
            className="secondary-button"
            onClick={() =>
              navigate(
                `/projects/${requirement?.projectId}/requirements`
              )
            }
          >
            Requirements
          </button>

          <button
            className="secondary-button"
            onClick={() =>
              navigate(
                `/projects/${requirement?.projectId}/dashboard`
              )
            }
          >
            Dashboard
          </button>
        </div>
      </header>

      <section className="section-header">
        <div>
          <h2>Requirement Test Cases</h2>

          <p>
            Define the scenarios used to verify this
            requirement.
          </p>
        </div>

        {canEdit && (
          <button
            className="primary-button"
            onClick={() =>
              navigate(
                `/requirements/${requirementId}/test-cases/new`
              )
            }
          >
            New Test Case
          </button>
        )}
      </section>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {testCases.length === 0 ? (
        <div className="empty-state">
          No test cases found for this requirement.
        </div>
      ) : (
        <div className="test-case-list">
          {testCases.map((testCase) => (
            <div
              key={testCase.id}
              className="test-case-card"
            >
              <div className="test-case-card-header">
                <div>
                  <span className="code-label">
                    {testCase.testCaseCode}
                  </span>

                  <h3>{testCase.title}</h3>
                </div>

                <div className="badge-group">
                  <span className="type-badge">
                    {testCase.type}
                  </span>

                  <span
                    className={`priority-badge priority-${testCase.priority?.toLowerCase()}`}
                  >
                    {testCase.priority}
                  </span>

                  <span
                    className={`status-badge status-${testCase.status?.toLowerCase()}`}
                  >
                    {testCase.status}
                  </span>
                </div>
              </div>

              <div className="test-case-details">
                <DetailBlock
                  title="Preconditions"
                  value={testCase.preconditions}
                />

                <DetailBlock
                  title="Test Steps"
                  value={testCase.testSteps}
                />

                <DetailBlock
                  title="Expected Result"
                  value={testCase.expectedResult}
                />
              </div>

              {canEdit && (
                <div className="card-actions">
                  <button
                    className="secondary-button"
                    onClick={() =>
                      navigate(
                        `/requirements/${requirementId}/test-cases/${testCase.id}/edit`
                      )
                    }
                  >
                    Edit
                  </button>

                  <button
                    className="danger-button"
                    onClick={() =>
                      handleDelete(testCase.id)
                    }
                  >
                    Delete
                  </button>
                </div>
              )}
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

      <p>
        {value || "Not specified."}
      </p>
    </div>
  );
}

export default TestCasesPage;