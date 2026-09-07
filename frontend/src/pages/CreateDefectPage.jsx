import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import api from "../api/api";

function CreateDefectPage() {
  const navigate = useNavigate();

  const { executionId } = useParams();

  const [execution, setExecution] =
    useState(null);

  const [title, setTitle] = useState("");
  const [description, setDescription] =
    useState("");
  const [severity, setSeverity] =
    useState("MEDIUM");
  const [assignedTo, setAssignedTo] =
    useState("");

  const [loading, setLoading] =
    useState(true);
  const [saving, setSaving] =
    useState(false);
  const [error, setError] = useState("");

  const user = JSON.parse(
    localStorage.getItem("user") || "{}"
  );

  const canCreate =
    user.role === "ADMIN" ||
    user.role === "TESTER";

  useEffect(() => {
    const loadExecution = async () => {
      try {
        const response = await api.get(
          `/test-executions/${executionId}`
        );

        setExecution(response.data);
      } catch (error) {
        setError(
          error.response?.data?.message ||
            "Execution could not be loaded."
        );
      } finally {
        setLoading(false);
      }
    };

    loadExecution();
  }, [executionId]);

  const handleSubmit = async (event) => {
    event.preventDefault();

    setSaving(true);
    setError("");

    try {
      await api.post(
        `/test-executions/${executionId}/defects`,
        {
          title,
          description,
          severity,
          assignedTo:
            assignedTo || null,
        }
      );

      navigate(
        `/test-runs/${execution.testRunId}/executions`
      );
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Defect could not be created."
      );
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        Loading execution...
      </div>
    );
  }

  if (!canCreate) {
    return (
      <div className="page-container">
        <p className="error-message">
          You do not have permission to create
          defects.
        </p>
      </div>
    );
  }

  if (execution?.result !== "FAILED") {
    return (
      <div className="page-container">
        <p className="error-message">
          A defect can only be created for a FAILED
          execution.
        </p>
      </div>
    );
  }

  return (
    <div className="page-container narrow-page">
      <button
        className="secondary-button"
        onClick={() =>
          navigate(
            `/test-runs/${execution.testRunId}/executions`
          )
        }
      >
        Back
      </button>

      <div className="form-card">
        <h1>Create Defect</h1>

        <div className="execution-reference">
          <strong>
            {execution.executionCode}
          </strong>

          <span>
            {execution.testCaseCode} ·{" "}
            {execution.testCaseTitle}
          </span>
        </div>

        <form onSubmit={handleSubmit}>
          <label>
            Title

            <input
              value={title}
              onChange={(event) =>
                setTitle(event.target.value)
              }
              required
            />
          </label>

          <label>
            Description

            <textarea
              value={description}
              onChange={(event) =>
                setDescription(
                  event.target.value
                )
              }
              rows={5}
              required
            />
          </label>

          <label>
            Severity

            <select
              value={severity}
              onChange={(event) =>
                setSeverity(
                  event.target.value
                )
              }
            >
              <option value="LOW">LOW</option>
              <option value="MEDIUM">
                MEDIUM
              </option>
              <option value="HIGH">
                HIGH
              </option>
              <option value="CRITICAL">
                CRITICAL
              </option>
            </select>
          </label>

          <label>
            Assigned To

            <input
              value={assignedTo}
              onChange={(event) =>
                setAssignedTo(
                  event.target.value
                )
              }
              placeholder="Developer or team name"
            />
          </label>

          {error && (
            <div className="error-message">
              {error}
            </div>
          )}

          <button
            className="primary-button"
            type="submit"
            disabled={saving}
          >
            {saving
              ? "Creating..."
              : "Create Defect"}
          </button>
        </form>
      </div>
    </div>
  );
}

export default CreateDefectPage;