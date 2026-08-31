import api from "./api";

export async function updateOrderStatus(orderId, status) {
  const response = await api.patch(
    `/restaurants/dashboard/orders/${orderId}/status`,
    { status }
  );
  return response.data;
}
