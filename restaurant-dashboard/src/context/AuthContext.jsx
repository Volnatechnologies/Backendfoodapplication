import { createContext, useCallback, useEffect, useMemo, useState } from "react";
import { storage } from "../utils/storage";
import { login as loginRequest } from "../services/authService";

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(storage.getToken());
  const [user, setUser] = useState(storage.getUser());

  const login = useCallback(async (credentials) => {
    const data = await loginRequest(credentials);

    // Supports common backend response names:
    // {token}, {accessToken}, or {jwt}
    const receivedToken = data.token || data.accessToken || data.jwt;
    if (!receivedToken) {
      throw new Error("Login succeeded but no JWT token was returned.");
    }

    const receivedUser = data.user || data.restaurantOwner || null;

    storage.setToken(receivedToken);
    if (receivedUser) storage.setUser(receivedUser);

    setToken(receivedToken);
    setUser(receivedUser);

    return data;
  }, []);

  const logout = useCallback(() => {
    storage.clear();
    setToken(null);
    setUser(null);
  }, []);

  useEffect(() => {
    const handler = () => logout();
    window.addEventListener("volna:unauthorized", handler);
    return () => window.removeEventListener("volna:unauthorized", handler);
  }, [logout]);

  const value = useMemo(
    () => ({
      token,
      user,
      isAuthenticated: Boolean(token),
      login,
      logout
    }),
    [token, user, login, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
