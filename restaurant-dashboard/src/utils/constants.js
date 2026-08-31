export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8083/api/v1";

export const AUTH_SERVICE_URL =
  import.meta.env.VITE_AUTH_SERVICE_URL || "http://localhost:8081/api/v1";

export const ORDER_STATUS = {
  NEW: "NEW",
  PREPARING: "PREPARING",
  READY: "READY",
  DELIVERED: "DELIVERED",
  DECLINED: "DECLINED",
  CANCELLED: "CANCELLED"
};
