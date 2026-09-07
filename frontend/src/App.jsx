import {
  BrowserRouter,
  Navigate,
  Route,
  Routes,
} from "react-router-dom";

import LoginPage from "./pages/LoginPage";
import ProjectsPage from "./pages/ProjectsPage";
import DashboardPage from "./pages/DashboardPage";

import CreateProjectPage from "./pages/CreateProjectPage";
import RequirementsPage from "./pages/RequirementsPage";
import CreateRequirementPage from "./pages/CreateRequirementPage";
import EditRequirementPage from "./pages/EditRequirementPage";

import TestCasesPage from "./pages/TestCasesPage";
import CreateTestCasePage from "./pages/CreateTestCasePage";
import EditTestCasePage from "./pages/EditTestCasePage";

import TestRunsPage from "./pages/TestRunsPage";
import CreateTestRunPage from "./pages/CreateTestRunPage";
import EditTestRunPage from "./pages/EditTestRunPage";

import TestExecutionsPage from "./pages/TestExecutionsPage";
import CreateTestExecutionPage from "./pages/CreateTestExecutionPage";
import EditTestExecutionPage from "./pages/EditTestExecutionPage";

import DefectsPage from "./pages/DefectsPage";
import CreateDefectPage from "./pages/CreateDefectPage";
import EditDefectPage from "./pages/EditDefectPage";

import ProtectedRoute from "./components/ProtectedRoute";


function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/login"
          element={<LoginPage />}
        />

        <Route
          path="/projects"
          element={
            <ProtectedRoute>
              <ProjectsPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/projects/new"
          element={
            <ProtectedRoute>
              <CreateProjectPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/projects/:projectId/dashboard"
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/projects/:projectId/requirements"
          element={
            <ProtectedRoute>
              <RequirementsPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/projects/:projectId/requirements/new"
          element={
            <ProtectedRoute>
              <CreateRequirementPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/requirements/:requirementId/edit"
          element={
            <ProtectedRoute>
              <EditRequirementPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/requirements/:requirementId/test-cases"
          element={
            <ProtectedRoute>
              <TestCasesPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/requirements/:requirementId/test-cases/new"
          element={
            <ProtectedRoute>
              <CreateTestCasePage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/requirements/:requirementId/test-cases/:testCaseId/edit"
          element={
            <ProtectedRoute>
              <EditTestCasePage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/projects/:projectId/test-runs"
          element={
            <ProtectedRoute>
              <TestRunsPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/projects/:projectId/test-runs/new"
          element={
            <ProtectedRoute>
              <CreateTestRunPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/projects/:projectId/test-runs/:testRunId/edit"
          element={
            <ProtectedRoute>
              <EditTestRunPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/test-runs/:testRunId/executions"
          element={
            <ProtectedRoute>
              <TestExecutionsPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/test-runs/:testRunId/executions/new"
          element={
            <ProtectedRoute>
              <CreateTestExecutionPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/test-runs/:testRunId/executions/:executionId/edit"
          element={
            <ProtectedRoute>
              <EditTestExecutionPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/projects/:projectId/defects"
          element={
            <ProtectedRoute>
              <DefectsPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/test-executions/:executionId/defects/new"
          element={
            <ProtectedRoute>
              <CreateDefectPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/projects/:projectId/defects/:defectId/edit"
          element={
            <ProtectedRoute>
              <EditDefectPage />
            </ProtectedRoute>
          }
        />

        <Route
          path="/"
          element={
            <Navigate
              to="/projects"
              replace
            />
          }
        />

        <Route
          path="*"
          element={
            <Navigate
              to="/projects"
              replace
            />
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;