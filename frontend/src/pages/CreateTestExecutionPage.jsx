import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import api from "../api/api";

function CreateTestExecutionPage() {
  const navigate = useNavigate();
  const { testRunId } = useParams();

  const [testRun, setTestRun] = useState(null);
  const [testCases, setTestCases] = useState([]);

  const [testCaseId, setTestCaseId] =
    useState("");

  const [loading, setLoading] =
    useState(true);

  const [saving, setSaving] =
    useState(false);

  const [error, setError] = useState("");

  const user = JSON.parse(
    localStorage.getItem("user") || "{}"
  );

  const canEdit =
    user.role === "ADMIN" ||
    user.role === "TESTER";

  useEffect(() => {
    const loadData = async () => {
      try {
        const testRunResponse =
          await api.get(
            `/test-runs/${testRunId}`
          );

        const run = testRunResponse.data;

        setTestRun(run);

        const requirementsResponse =
          await api.get(
            `/projects/${run.projectId}/requirements`
          );

        const requirements =
          requirementsResponse.data;

        const testCaseRequests =
          requirements.map((requirement) =>
            api.get(
              `/requirements/${requirement.id}/test-cases`
            )
          );

        const responses =
          await Promise.all(testCaseRequests);

        const allTestCases =
          responses.flatMap(
            (response) => response.data
          );

        setTestCases(allTestCases);

        if (allTestCases.length > 0) {
          setTestCaseId(
            String(allTestCases[0].id)
          );
        }
      } catch (error) {
        setError(
          error.response?.data?.message ||
            "Available test cases could not be loaded."
        );
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [testRunId]);

  const handleSubmit = async (event) => {
    event.preventDefault();

    setSaving(true);
    setError("");

    try {
      await api.post(
        `/test-runs/${testRunId}/executions`,
        {
          testCaseId: Number(testCaseId),
        }
      );

      navigate(
        `/test-runs/${testRunId}/executions`
      );
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Execution could not be created."
      );
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        Loading test cases...
      </div>
    );
  }

  if (!canEdit) {
    return (
      <div className="page-container">
        <p className="error-message">
          You do not have permission to create
          executions.
        </p>
      </div>
    );
  }

  if (
    testRun?.status === "COMPLETED" ||
    testRun?.status === "CANCELLED"
  ) {
    return (
      <div className="page-container">
        <p className="error-message">
          Executions cannot be added to a completed
          or cancelled test run.
        </p>

        <button
          className="secondary-button"
          onClick={() =>
            navigate(
              `/test-runs/${testRunId}/executions`
            )
          }
        >
          Back
        </button>
      </div>
    );
  }

  return (
    <div className="page-container narrow-page">
      <button
        className="secondary-button"
        onClick={() =>
          navigate(
            `/test-runs/${testRunId}/executions`
          )
        }
      >
        Back
      </button>

      <div className="form-card">
        <h1>Add Test Case to Run</h1>

        <p>
          {testRun?.runCode} · {testRun?.name}
        </p>

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        {testCases.length === 0 ? (
          <div className="empty-state">
            This project has no test cases available.
          </div>
        ) : (
          <form onSubmit={handleSubmit}>
            <label>
              Test Case

              <select
                value={testCaseId}
                onChange={(event) =>
                  setTestCaseId(
                    event.target.value
                  )
                }
              >
                {testCases.map((testCase) => (
                  <option
                    key={testCase.id}
                    value={testCase.id}
                  >
                    {testCase.testCaseCode} —{" "}
                    {testCase.title}
                  </option>
                ))}
              </select>
            </label>

            <p className="helper-text">
              A test case can only be added once to
              the same test run.
            </p>

            <button
              className="primary-button"
              type="submit"
              disabled={saving}
            >
              {saving
                ? "Adding..."
                : "Add Test Case"}
            </button>
          </form>
        )}
      </div>
    </div>
  );
}

export default CreateTestExecutionPage;