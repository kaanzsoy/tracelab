import { useEffect, useState } from "react";
import {
  useNavigate,
  useParams,
} from "react-router-dom";

import api from "../api/api";

function DashboardPage() {
  const navigate = useNavigate();
  const { projectId } = useParams();

  const [dashboard, setDashboard] =
    useState(null);

  const [error, setError] = useState("");
  const [loading, setLoading] =
    useState(true);

  const user = JSON.parse(
    localStorage.getItem("user") || "{}"
  );

  useEffect(() => {
    const loadDashboard = async () => {
      try {
        setError("");

        const response = await api.get(
          `/projects/${projectId}/dashboard`
        );

        setDashboard(response.data);
      } catch (error) {
        setError(
          error.response?.data?.message ||
            "Dashboard could not be loaded."
        );
      } finally {
        setLoading(false);
      }
    };

    loadDashboard();
  }, [projectId]);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");

    navigate("/login");
  };

  if (loading) {
    return (
      <div className="page-container">
        Loading dashboard...
      </div>
    );
  }

  if (error) {
    return (
      <div className="page-container">
        <button
          className="secondary-button"
          onClick={() => navigate("/projects")}
        >
          Back to Projects
        </button>

        <p className="error-message">
          {error}
        </p>
      </div>
    );
  }

  return (
    <div className="page-container">
      <header className="top-bar">
        <div>
          <h1>TraceLab Dashboard</h1>

          <p>
            Welcome, {user.fullName || user.username}
          </p>
        </div>

        <div className="header-actions">
          <button
            className="secondary-button"
            onClick={() => navigate("/projects")}
          >
            Projects
          </button>

          <button
            className="secondary-button"
            onClick={handleLogout}
          >
            Logout
          </button>
        </div>
      </header>

      <section className="project-header">
        <div>
          <h2>{dashboard.projectName}</h2>

          <span>
            Project ID: {dashboard.projectId}
          </span>
        </div>

        <div className="project-actions">
          <button
            className="primary-button"
            onClick={() =>
              navigate(
                `/projects/${projectId}/requirements`
              )
            }
          >
            Requirements
          </button>

          <button
            className="primary-button"
            onClick={() =>
                navigate(
                `/projects/${projectId}/test-runs`
                )
            }
          >
            Test Runs
          </button>

          <button
            className="primary-button"
            onClick={() =>
              navigate(
                `/projects/${projectId}/defects`
              )
            }
          >
            Defects
          </button>

        </div>
      </section>

      <section className="metrics-grid">
        <MetricCard
          title="Requirements"
          value={dashboard.totalRequirements}
        />

        <MetricCard
          title="Requirement Coverage"
          value={`${dashboard.requirementCoverageRate}%`}
        />

        <MetricCard
          title="Test Cases"
          value={dashboard.totalTestCases}
        />

        <MetricCard
          title="Test Runs"
          value={dashboard.totalTestRuns}
        />

        <MetricCard
          title="Executions"
          value={dashboard.totalExecutions}
        />

        <MetricCard
          title="Pass Rate"
          value={`${dashboard.passRate}%`}
        />

        <MetricCard
          title="Failed Executions"
          value={dashboard.failedExecutions}
        />

        <MetricCard
          title="Active Defects"
          value={dashboard.activeDefects}
        />
      </section>

      <section className="dashboard-section">
        <h2>Execution Summary</h2>

        <div className="summary-grid">
          <SummaryItem
            label="Passed"
            value={dashboard.passedExecutions}
          />

          <SummaryItem
            label="Failed"
            value={dashboard.failedExecutions}
          />

          <SummaryItem
            label="Blocked"
            value={dashboard.blockedExecutions}
          />

          <SummaryItem
            label="Not Run"
            value={dashboard.notRunExecutions}
          />
        </div>
      </section>

      <section className="dashboard-section">
        <h2>Defects by Severity</h2>

        <div className="summary-grid">
          <SummaryItem
            label="Low"
            value={dashboard.defectsBySeverity.low}
          />

          <SummaryItem
            label="Medium"
            value={dashboard.defectsBySeverity.medium}
          />

          <SummaryItem
            label="High"
            value={dashboard.defectsBySeverity.high}
          />

          <SummaryItem
            label="Critical"
            value={dashboard.defectsBySeverity.critical}
          />
        </div>
      </section>
    </div>
  );
}

function MetricCard({ title, value }) {
  return (
    <div className="metric-card">
      <span>{title}</span>
      <strong>{value}</strong>
    </div>
  );
}

function SummaryItem({ label, value }) {
  return (
    <div className="summary-item">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

export default DashboardPage;