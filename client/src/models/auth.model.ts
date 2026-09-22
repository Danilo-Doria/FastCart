// deben ser iguales a los models del backend (java-springboot)
export interface LoginRequest {
  username: string; // o email según tu Spring Security
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number; // 'long' en Java se mapea a 'number' en TS
}

export interface RefreshTokenRequest {
  refreshToken: string;
}