import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import api from "../api/api";

function EditDefectPage() {
  const navigate = useNavigate();

  const {
    projectId,
    defectId,
  } = useParams();

  const [defect, setDefect] =
    useState(null);

  const [title, setTitle] = useState("");
  const [description, setDescription] =
    useState("");
  const [severity, setSeverity] =
    useState("MEDIUM");
  const [status, setStatus] =
    useState("OPEN");
  const [assignedTo, setAssignedTo] =
    useState("");
  const [
    resolutionNotes,
    setResolutionNotes,
  ] = useState("");

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
    user.role === "TESTER" ||
    user.role === "DEVELOPER";

  useEffect(() => {
    const loadDefect = async () => {
      try {
        const response = await api.get(
          `/defects/${defectId}`
        );

        const data = response.data;

        setDefect(data);
        setTitle(data.title || "");
        setDescription(
          data.description || ""
        );
        setSeverity(data.severity);
        setStatus(data.status);
        setAssignedTo(
          data.assignedTo || ""
        );
        setResolutionNotes(
          data.resolutionNotes || ""
        );
      } catch (error) {
        setError(
          error.response?.data?.message ||
            "Defect could not be loaded."
        );
      } finally {
        setLoading(false);
      }
    };

    loadDefect();
  }, [defectId]);

  const handleSubmit = async (event) => {
    event.preventDefault();

    setSaving(true);
    setError("");

    try {
      await api.put(
        `/defects/${defectId}`,
        {
          title,
          description,
          severity,
          status,
          assignedTo:
            assignedTo || null,
          resolutionNotes:
            resolutionNotes || null,
        }
      );

      navigate(
        `/projects/${projectId}/defects`
      );
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Defect could not be updated."
      );
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        Loading defect...
      </div>
    );
  }

  if (!canEdit) {
    return (
      <div className="page-container">
        <p className="error-message">
          You do not have permission to update
          defects.
        </p>
      </div>
    );
  }

  if (error && !defect) {
    return (
      <div className="page-container">
        <p className="error-message">
          {error}
        </p>
      </div>
    );
  }

  const resolutionRequired =
    status === "RESOLVED" ||
    status === "CLOSED";

  return (
    <div className="page-container narrow-page">
      <button
        className="secondary-button"
        onClick={() =>
          navigate(
            `/projects/${projectId}/defects`
          )
        }
      >
        Back
      </button>

      <div className="form-card">
        <h1>Edit Defect</h1>

        <p>{defect.defectCode}</p>

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
            Status

            <select
              value={status}
              onChange={(event) =>
                setStatus(
                  event.target.value
                )
              }
            >
              <option value="OPEN">
                OPEN
              </option>

              <option value="IN_PROGRESS">
                IN_PROGRESS
              </option>

              <option value="RESOLVED">
                RESOLVED
              </option>

              <option value="CLOSED">
                CLOSED
              </option>

              <option value="REOPENED">
                REOPENED
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
            />
          </label>

          <label>
            Resolution Notes

            <textarea
              value={resolutionNotes}
              onChange={(event) =>
                setResolutionNotes(
                  event.target.value
                )
              }
              rows={5}
              required={resolutionRequired}
            />
          </label>

          {resolutionRequired && (
            <p className="helper-text">
              Resolution Notes are required when a
              defect is RESOLVED or CLOSED.
            </p>
          )}

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
              : "Save Defect"}
          </button>
        </form>
      </div>
    </div>
  );
}

export default EditDefectPage;