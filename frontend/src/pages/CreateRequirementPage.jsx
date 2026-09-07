import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import api from "../api/api";

function CreateRequirementPage() {
  const navigate = useNavigate();
  const { projectId } = useParams();

  const [title, setTitle] = useState("");
  const [description, setDescription] =
    useState("");
  const [priority, setPriority] =
    useState("MEDIUM");
  const [status, setStatus] =
    useState("DRAFT");

  const [error, setError] = useState("");
  const [loading, setLoading] =
    useState(false);

  const user = JSON.parse(
    localStorage.getItem("user") || "{}"
  );

  const canEdit =
    user.role === "ADMIN" ||
    user.role === "TESTER";

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setLoading(true);

    try {
      await api.post(
        `/projects/${projectId}/requirements`,
        {
          title,
          description,
          priority,
          status,
        }
      );

      navigate(
        `/projects/${projectId}/requirements`
      );
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Requirement could not be created."
      );
    } finally {
      setLoading(false);
    }
  };

  if (!canEdit) {
    return (
      <div className="page-container">
        <p className="error-message">
          You do not have permission to create
          requirements.
        </p>

        <button
          className="secondary-button"
          onClick={() =>
            navigate(
              `/projects/${projectId}/requirements`
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
            `/projects/${projectId}/requirements`
          )
        }
      >
        Back
      </button>

      <div className="form-card">
        <h1>Create Requirement</h1>

        <form onSubmit={handleSubmit}>
          <label>
            Title

            <input
              value={title}
              onChange={(event) =>
                setTitle(event.target.value)
              }
              required
              maxLength={200}
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
              maxLength={2000}
            />
          </label>

          <label>
            Priority

            <select
              value={priority}
              onChange={(event) =>
                setPriority(
                  event.target.value
                )
              }
            >
              <option value="LOW">LOW</option>
              <option value="MEDIUM">
                MEDIUM
              </option>
              <option value="HIGH">HIGH</option>
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
                setStatus(event.target.value)
              }
            >
              <option value="DRAFT">
                DRAFT
              </option>
              <option value="APPROVED">
                APPROVED
              </option>
              <option value="DEPRECATED">
                DEPRECATED
              </option>
            </select>
          </label>

          {error && (
            <div className="error-message">
              {error}
            </div>
          )}

          <button
            className="primary-button"
            disabled={loading}
          >
            {loading
              ? "Creating..."
              : "Create Requirement"}
          </button>
        </form>
      </div>
    </div>
  );
}

export default CreateRequirementPage;