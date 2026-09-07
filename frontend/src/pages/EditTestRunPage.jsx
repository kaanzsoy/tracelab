import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import api from "../api/api";

function EditTestRunPage() {
  const navigate = useNavigate();

  const {
    projectId,
    testRunId,
  } = useParams();

  const [testRun, setTestRun] =
    useState(null);

  const [name, setName] = useState("");
  const [description, setDescription] =
    useState("");

  const [environment, setEnvironment] =
    useState("TEST");

  const [status, setStatus] =
    useState("PLANNED");

  const [startedAt, setStartedAt] =
    useState("");

  const [completedAt, setCompletedAt] =
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
    const loadTestRun = async () => {
      try {
        const response = await api.get(
          `/test-runs/${testRunId}`
        );

        const data = response.data;

        setTestRun(data);
        setName(data.name || "");
        setDescription(
          data.description || ""
        );
        setEnvironment(
          data.environment
        );
        setStatus(data.status);

        setStartedAt(
          toDateTimeLocal(
            data.startedAt
          )
        );

        setCompletedAt(
          toDateTimeLocal(
            data.completedAt
          )
        );
      } catch (error) {
        setError(
          error.response?.data?.message ||
            "Test run could not be loaded."
        );
      } finally {
        setLoading(false);
      }
    };

    loadTestRun();
  }, [testRunId]);

  const handleSubmit = async (event) => {
    event.preventDefault();

    setSaving(true);
    setError("");

    try {
      await api.put(
        `/test-runs/${testRunId}`,
        {
          name,
          description,
          environment,
          status,
          startedAt:
            startedAt || null,
          completedAt:
            completedAt || null,
        }
      );

      navigate(
        `/projects/${projectId}/test-runs`
      );
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Test run could not be updated."
      );
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        Loading test run...
      </div>
    );
  }

  if (error && !testRun) {
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
          You do not have permission to edit
          test runs.
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
            `/projects/${projectId}/test-runs`
          )
        }
      >
        Back
      </button>

      <div className="form-card">
        <h1>Edit Test Run</h1>

        <p>{testRun.runCode}</p>

        <form onSubmit={handleSubmit}>
          <label>
            Name

            <input
              value={name}
              onChange={(event) =>
                setName(event.target.value)
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
              rows={4}
            />
          </label>

          <label>
            Environment

            <select
              value={environment}
              onChange={(event) =>
                setEnvironment(
                  event.target.value
                )
              }
            >
              <option value="DEVELOPMENT">
                DEVELOPMENT
              </option>

              <option value="TEST">
                TEST
              </option>

              <option value="STAGING">
                STAGING
              </option>

              <option value="PRODUCTION">
                PRODUCTION
              </option>
            </select>
          </label>

          <label>
            Status

            <select
              value={status}
              onChange={(event) =>
                setStatus(event.target.value)
              }
            >
              <option value="PLANNED">
                PLANNED
              </option>

              <option value="IN_PROGRESS">
                IN_PROGRESS
              </option>

              <option value="COMPLETED">
                COMPLETED
              </option>

              <option value="CANCELLED">
                CANCELLED
              </option>
            </select>
          </label>

          <label>
            Started At

            <input
              type="datetime-local"
              value={startedAt}
              onChange={(event) =>
                setStartedAt(
                  event.target.value
                )
              }
            />
          </label>

          <label>
            Completed At

            <input
              type="datetime-local"
              value={completedAt}
              onChange={(event) =>
                setCompletedAt(
                  event.target.value
                )
              }
            />
          </label>

          {error && (
            <div className="error-message">
              {error}
            </div>
          )}

          <button
            className="primary-button"
            disabled={saving}
          >
            {saving
              ? "Saving..."
              : "Save Changes"}
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

export default EditTestRunPage;