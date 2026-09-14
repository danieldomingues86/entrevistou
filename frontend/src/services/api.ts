import type { DashboardData, LearningPath, StudySession, StudySessionResult } from '../types';

const API = 'http://localhost:8080/api';

async function json<T>(url: string, init?: RequestInit): Promise<T> {
  const res = await fetch(url, init);
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return res.json() as Promise<T>;
}

export const getDashboard = () => json<DashboardData>(`${API}/dashboard`);
export const getLearningPath = () => json<LearningPath>(`${API}/learning-path`);
export const getStudySession = (slug: string) => json<StudySession>(`${API}/study/${slug}`);
export const submitStudySession = (slug: string, answers: Record<string,string>) =>
  json<StudySessionResult>(`${API}/study/${slug}/submit`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ answers })
  });
