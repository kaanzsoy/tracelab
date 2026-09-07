import { useState } from "react";
import { useNavigate } from "react-router-dom";

import api from "../api/api";

function CreateProjectPage() {
  const navigate = useNavigate();

  const [name, setName] = useState("");
  const [description, setDescription] =
    useState("");

  const [error, setError] = useState("");
  const [loading, setLoading] =
    useState(false);

  const user = JSON.parse(
    localStorage.getItem("user") || "{}"
  );

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setLoading(true);

    try {
      const response = await api.post(
        "/projects",
        {
          name,
          description,
          status: "ACTIVE",
        }
      );

      navigate(
        `/projects/${response.data.id}/dashboard`
      );
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Project could not be created."
      );
    } finally {
      setLoading(false);
    }
  };

  if (user.role !== "ADMIN") {
    return (
      <div className="page-container">
        <p className="error-message">
          Only ADMIN users can create projects.
        </p>

        <button
          className="secondary-button"
          onClick={() => navigate("/projects")}
        >
          Back to Projects
        </button>
      </div>
    );
  }

  return (
    <div className="page-container narrow-page">
      <button
        className="secondary-button"
        onClick={() => navigate("/projects")}
      >
        Back
      </button>

      <div className="form-card">
        <h1>Create Project</h1>

        <p>
          Create a new TraceLab project.
        </p>

        <form onSubmit={handleSubmit}>
          <label>
            Project Name

            <input
              type="text"
              value={name}
              onChange={(event) =>
                setName(event.target.value)
              }
              required
              maxLength={150}
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
              maxLength={1000}
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
            disabled={loading}
          >
            {loading
              ? "Creating..."
              : "Create Project"}
          </button>
        </form>
      </div>
    </div>
  );
}

export default CreateProjectPage;