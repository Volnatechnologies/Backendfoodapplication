import api from "./api";

export async function getMenu() {
  const response = await api.get("/restaurants/menu");
  return response.data;
}
