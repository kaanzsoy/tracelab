import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import api from "../api/api";

function EditTestExecutionPage() {
  const navigate = useNavigate();

  const {
    testRunId,
    executionId,
  } = useParams();

  const [execution, setExecution] =
    useState(null);

  const [result, setResult] =
    useState("NOT_RUN");

  const [actualResult, setActualResult] =
    useState("");

  const [notes, setNotes] =
    useState("");

  const [executedAt, setExecutedAt] =
    useState("");

  const [loading, setLoading] =
    useState(true);

  const [saving, setSaving] =
    useState(false);

  const [error, setError] =
    useState("");

  const user = JSON.parse(
    localStorage.getItem("user") || "{}"
  );

  const canEdit =
    user.role === "ADMIN" ||
    user.role === "TESTER";

  useEffect(() => {
    const loadExecution = async () => {
      try {
        const response = await api.get(
          `/test-executions/${executionId}`
        );

        const data = response.data;

        setExecution(data);
        setResult(data.result);
        setActualResult(
          data.actualResult || ""
        );
        setNotes(
          data.notes || ""
        );
        setExecutedAt(
          toDateTimeLocal(
            data.executedAt
          )
        );
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

  const handleResultChange = (value) => {
    setResult(value);

    if (value === "NOT_RUN") {
      setExecutedAt("");
      setActualResult("");
    } else if (!executedAt) {
      setExecutedAt(
        toCurrentDateTimeLocal()
      );
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setSaving(true);
    setError("");

    try {
      await api.put(
        `/test-executions/${executionId}`,
        {
          result,
          actualResult:
            actualResult || null,
          notes:
            notes || null,
          executedAt:
            executedAt || null,
        }
      );

      navigate(
        `/test-runs/${testRunId}/executions`
      );
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Execution could not be updated."
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

  if (error && !execution) {
    return (
      <div className="page-container">
        <p className="error-message">
          {error}
        </p>
      </div>
    );
  }

  if (!canEdit) {
    return (
      <div className="page-container">
        <p className="error-message">
          You do not have permission to update
          executions.
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
            `/test-runs/${testRunId}/executions`
          )
        }
      >
        Back
      </button>

      <div className="form-card">
        <h1>Edit Execution</h1>

        <p>
          {execution.executionCode}
        </p>

        <div className="execution-reference">
          <strong>
            {execution.testCaseCode}
          </strong>

          <span>
            {execution.testCaseTitle}
          </span>
        </div>

        <form onSubmit={handleSubmit}>
          <label>
            Result

            <select
              value={result}
              onChange={(event) =>
                handleResultChange(
                  event.target.value
                )
              }
            >
              <option value="NOT_RUN">
                NOT_RUN
              </option>

              <option value="PASSED">
                PASSED
              </option>

              <option value="FAILED">
                FAILED
              </option>

              <option value="BLOCKED">
                BLOCKED
              </option>
            </select>
          </label>

          <label>
            Actual Result

            <textarea
              value={actualResult}
              onChange={(event) =>
                setActualResult(
                  event.target.value
                )
              }
              rows={5}
              disabled={result === "NOT_RUN"}
              required={result === "FAILED"}
            />
          </label>

          {result === "FAILED" && (
            <p className="helper-text">
              Actual Result is required for FAILED
              executions.
            </p>
          )}

          <label>
            Notes

            <textarea
              value={notes}
              onChange={(event) =>
                setNotes(
                  event.target.value
                )
              }
              rows={4}
            />
          </label>

          <label>
            Executed At

            <input
              type="datetime-local"
              value={executedAt}
              onChange={(event) =>
                setExecutedAt(
                  event.target.value
                )
              }
              disabled={result === "NOT_RUN"}
              required={result !== "NOT_RUN"}
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
              ? "Saving..."
              : "Save Execution"}
          </button>
        </form>
      </div>
    </div>
  );
}

function toDateTimeLocal(value) {
  if (!value) {
    return "";
  }

  return value.slice(0, 16);
}

function toCurrentDateTimeLocal() {
  const now = new Date();

  const offset =
    now.getTimezoneOffset();

  const localDate = new Date(
    now.getTime() - offset * 60 * 1000
  );

  return localDate
    .toISOString()
    .slice(0, 16);
}

export default EditTestExecutionPage;