import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import api from "../api/api";

function EditRequirementPage() {
  const navigate = useNavigate();
  const { requirementId } = useParams();

  const [requirement, setRequirement] =
    useState(null);

  const [title, setTitle] = useState("");
  const [description, setDescription] =
    useState("");
  const [priority, setPriority] =
    useState("MEDIUM");
  const [status, setStatus] =
    useState("DRAFT");

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
    const loadRequirement = async () => {
      try {
        const response = await api.get(
          `/requirements/${requirementId}`
        );

        const data = response.data;

        setRequirement(data);
        setTitle(data.title);
        setDescription(
          data.description || ""
        );
        setPriority(data.priority);
        setStatus(data.status);
      } catch (error) {
        setError(
          error.response?.data?.message ||
            "Requirement could not be loaded."
        );
      } finally {
        setLoading(false);
      }
    };

    loadRequirement();
  }, [requirementId]);

  const handleSubmit = async (event) => {
    event.preventDefault();

    setSaving(true);
    setError("");

    try {
      await api.put(
        `/requirements/${requirementId}`,
        {
          title,
          description,
          priority,
          status,
        }
      );

      navigate(
        `/projects/${requirement.projectId}/requirements`
      );
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Requirement could not be updated."
      );
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        Loading requirement...
      </div>
    );
  }

  if (error && !requirement) {
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
          requirements.
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
            `/projects/${requirement.projectId}/requirements`
          )
        }
      >
        Back
      </button>

      <div className="form-card">
        <h1>Edit Requirement</h1>

        <p>
          {requirement.requirementCode}
        </p>

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

export default EditRequirementPage;