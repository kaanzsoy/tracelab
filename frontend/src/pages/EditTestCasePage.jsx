import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import api from "../api/api";

function EditTestCasePage() {
  const navigate = useNavigate();

  const {
    requirementId,
    testCaseId,
  } = useParams();

  const [testCase, setTestCase] =
    useState(null);

  const [title, setTitle] = useState("");
  const [preconditions, setPreconditions] =
    useState("");
  const [testSteps, setTestSteps] =
    useState("");
  const [expectedResult, setExpectedResult] =
    useState("");

  const [type, setType] =
    useState("FUNCTIONAL");
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
    const loadTestCase = async () => {
      try {
        const response = await api.get(
          `/test-cases/${testCaseId}`
        );

        const data = response.data;

        setTestCase(data);
        setTitle(data.title || "");
        setPreconditions(
          data.preconditions || ""
        );
        setTestSteps(
          data.testSteps || ""
        );
        setExpectedResult(
          data.expectedResult || ""
        );
        setType(data.type);
        setPriority(data.priority);
        setStatus(data.status);
      } catch (error) {
        setError(
          error.response?.data?.message ||
            "Test case could not be loaded."
        );
      } finally {
        setLoading(false);
      }
    };

    loadTestCase();
  }, [testCaseId]);

  const handleSubmit = async (event) => {
    event.preventDefault();

    setSaving(true);
    setError("");

    try {
      await api.put(
        `/test-cases/${testCaseId}`,
        {
          title,
          preconditions,
          testSteps,
          expectedResult,
          type,
          priority,
          status,
        }
      );

      navigate(
        `/requirements/${requirementId}/test-cases`
      );
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Test case could not be updated."
      );
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="page-container">
        Loading test case...
      </div>
    );
  }

  if (error && !testCase) {
    return (
      <div className="page-container">
        <p className="error-message">
          {error}
        </p>

        <button
          className="secondary-button"
          onClick={() =>
            navigate(
              `/requirements/${requirementId}/test-cases`
            )
          }
        >
          Back
        </button>
      </div>
    );
  }

  if (!canEdit) {
    return (
      <div className="page-container">
        <p className="error-message">
          You do not have permission to edit test
          cases.
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
            `/requirements/${requirementId}/test-cases`
          )
        }
      >
        Back
      </button>

      <div className="form-card">
        <h1>Edit Test Case</h1>

        <p>
          {testCase.testCaseCode}
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
            Preconditions

            <textarea
              value={preconditions}
              onChange={(event) =>
                setPreconditions(
                  event.target.value
                )
              }
              rows={3}
            />
          </label>

          <label>
            Test Steps

            <textarea
              value={testSteps}
              onChange={(event) =>
                setTestSteps(
                  event.target.value
                )
              }
              rows={6}
              required
            />
          </label>

          <label>
            Expected Result

            <textarea
              value={expectedResult}
              onChange={(event) =>
                setExpectedResult(
                  event.target.value
                )
              }
              rows={4}
              required
            />
          </label>

          <label>
            Type

            <select
              value={type}
              onChange={(event) =>
                setType(event.target.value)
              }
            >
              <option value="FUNCTIONAL">
                FUNCTIONAL
              </option>

              <option value="NEGATIVE">
                NEGATIVE
              </option>

              <option value="REGRESSION">
                REGRESSION
              </option>

              <option value="INTEGRATION">
                INTEGRATION
              </option>
            </select>
          </label>

          <label>
            Priority

            <select
              value={priority}
              onChange={(event) =>
                setPriority(event.target.value)
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

              <option value="READY">
                READY
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
            type="submit"
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

export default EditTestCasePage;