import api from "./api";

export async function getRestaurantDashboard() {
  const response = await api.get("/restaurants/dashboard");
  return response.data;
}

export async function seedDashboardDemoData() {
  const response = await api.post("/restaurants/dashboard/dev/seed");
  return response.data;
}
