import { useEffect, useMemo, useState } from 'react';
import { getDashboard, getLearningPath, getStudySession, submitStudySession } from './services/api';
import type { DashboardData, LearningModule, LearningPath, StudySession, StudySessionResult } from './types';

type View = { kind: 'dashboard' } | { kind: 'study'; slug: string };

const moduleGlyph: Record<string,string> = {
  'Java Core': '</>', Spring: 'S', Kafka: 'K', SQL: 'DB', 'System Design': 'SD', 'Mock Interview': 'MI'
};

export default function App() {
  const [signedIn, setSignedIn] = useState(false);
  const [view, setView] = useState<View>({ kind: 'dashboard' });
  const [dashboard, setDashboard] = useState<DashboardData | null>(null);
  const [path, setPath] = useState<LearningPath | null>(null);
  const [loading, setLoading] = useState(true);

  const reload = async () => {
    setLoading(true);
    try {
      const [d, p] = await Promise.all([getDashboard(), getLearningPath()]);
      setDashboard(d); setPath(p);
    } finally { setLoading(false); }
  };

  useEffect(() => { void reload(); }, []);

  if (!signedIn) return <LoginPage onEnter={() => setSignedIn(true)} />;
  if (view.kind === 'study') return <StudyPage slug={view.slug} onBack={async () => { await reload(); setView({kind:'dashboard'}); }} />;
  return <Dashboard loading={loading} dashboard={dashboard} path={path} onStudy={(slug) => slug && setView({kind:'study', slug})} />;
}

function LoginPage({ onEnter }: { onEnter: () => void }) {
  const [showPassword, setShowPassword] = useState(false);
  return <div className="login-page">
    <section className="login-brand-panel">
      <div className="login-brand-top"><div className="logo-bubble">E</div><div><strong>entrevistou</strong><span>SUA PRÓXIMA OPORTUNIDADE COMEÇA AQUI</span></div></div>
      <div className="login-pitch">
        <h1><span>Estudou.</span><b>Praticou.</b><span>Entrevistou.</span></h1>
        <p>Conteúdo, prática e confiança para você conquistar a vaga que merece.</p>
        <div className="login-features">
          <Feature glyph="▤" text="Trilhas de estudo"/><Feature glyph="</>" text="Questões reais"/><Feature glyph="⌨" text="Ambiente de código"/><Feature glyph="▥" text="Simulados e feedback"/><Feature glyph="◎" text="Do estudo à aprovação"/>
        </div>
      </div>
      <div className="login-motto">✦ Grandes carreiras começam com boa preparação.</div>
    </section>
    <section className="login-form-panel">
      <div className="login-quote">“Mais que um software.<br/>Um parceiro na sua evolução.”</div>
      <div className="login-card">
        <h2>Bem-vindo de volta</h2><p>Acesse sua conta e continue sua jornada.</p>
        <label>E-mail</label><div className="login-input"><span>✉</span><input placeholder="seu@email.com"/></div>
        <div className="password-head"><label>Senha</label><button type="button">Esqueceu sua senha?</button></div>
        <div className="login-input"><span>▣</span><input type={showPassword?'text':'password'} placeholder="Digite sua senha"/><button type="button" onClick={() => setShowPassword(v => !v)}>{showPassword?'Ocultar':'Ver'}</button></div>
        <button className="login-enter" onClick={onEnter}>Entrar <span>→</span></button>
        <div className="login-separator"><span>ou continue com</span></div>
        <div className="social-login"><button onClick={onEnter}><b>G</b> Google</button><button onClick={onEnter}><b>GH</b> GitHub</button><button onClick={onEnter}><b>in</b> LinkedIn</button></div>
        <div className="login-signup">Ainda não tem uma conta? <button onClick={onEnter}>Criar conta</button></div>
      </div>
      <div className="login-trust"><span>◇ Seguro e confiável</span><span>ϟ Foco no que importa</span><span>♙ Feito por devs para devs</span></div>
    </section>
  </div>;
}

function Feature({glyph,text}:{glyph:string;text:string}) { return <div><i>{glyph}</i><span>{text}</span></div>; }

function Dashboard({loading,dashboard,path,onStudy}:{loading:boolean;dashboard:DashboardData|null;path:LearningPath|null;onStudy:(slug:string|null)=>void}) {
  const [query,setQuery]=useState('');
  const modules=(path?.modules??[]).filter(m => (m.category+' '+m.description).toLowerCase().includes(query.toLowerCase()));
  const focus=[...(path?.modules??[])].sort((a,b)=>a.mastery-b.mastery)[0];
  return <div className="app-shell">
    <aside className="sidebar">
      <div className="brand"><div className="brand-mark">E</div><div><strong>Entrevistou</strong><span>Java Senior</span></div></div>
      <nav><button className="active">⌂ Início</button><button>▤ Trilha de estudos</button><button>▧ Question Bank</button><button>&lt;/&gt; Code Lab</button><button>⌘ System Design</button><button>◉ Mock Interview</button></nav>
      <div className="sidebar-card"><span>Prontidão atual</span><strong>{dashboard?.readiness??0}%</strong><Progress value={dashboard?.readiness??0}/><small>Baseado no domínio dos tópicos.</small></div>
    </aside>
    <main className="main-content">
      <header className="topbar"><div><p className="eyebrow">ENTREVISTOU</p><h1>Prepare-se para entrevistas <span>sem perder tempo.</span></h1><p>Conteúdo organizado, prática guiada e progresso em um único lugar.</p></div><div className="avatar">DG</div></header>
      <div className="search"><span>⌕</span><input value={query} onChange={e=>setQuery(e.target.value)} placeholder="Buscar Java, Spring, Kafka, SQL..."/></div>
      {loading ? <div className="loading">Carregando sua trilha...</div> : <>
        <section className="hero-grid"><article className="continue-card"><div><span className="pill">PRÓXIMA RECOMENDAÇÃO</span><h2>{focus?.category}</h2><p>{focus?.description}</p></div><div className="continue-footer"><div><small>Domínio atual</small><strong>{focus?.mastery}%</strong></div><button onClick={()=>onStudy(focus?.featuredSessionSlug??null)}>Continuar estudando</button></div></article><article className="readiness-card"><span className="gauge">◔</span><span>Interview Readiness</span><strong>{dashboard?.readiness}%</strong><Progress value={dashboard?.readiness??0}/><small>Trilha Java Senior Backend</small></article></section>
        <section className="section-head"><div><p className="eyebrow">SUA TRILHA</p><h2>Java Senior Backend</h2></div><span>{path?.modules.length} módulos</span></section>
        <section className="module-grid">{modules.map(m=><ModuleCard key={m.category} module={m} onStudy={onStudy}/>)}</section>
        <section className="section-head"><div><p className="eyebrow">VISÃO GERAL</p><h2>Domínio por tópico</h2></div></section>
        <section className="topic-list">{dashboard?.topics.map(t=><div className="topic-row" key={t.id}><div><strong>{t.title}</strong><span>{t.category}</span></div><Progress value={t.mastery}/><b>{t.mastery}%</b></div>)}</section>
      </>}
    </main>
  </div>;
}

function Progress({value}:{value:number}) { return <div className="progress"><i style={{width:`${Math.max(0,Math.min(100,value))}%`}}/></div>; }

function ModuleCard({module,onStudy}:{module:LearningModule;onStudy:(slug:string|null)=>void}) {
  return <article className="module-card"><div className="module-top"><div className="module-icon">{moduleGlyph[module.category]??'•'}</div><span className="status">{module.status}</span></div><h3>{module.category}</h3><p>{module.description}</p><div className="module-progress"><Progress value={module.mastery}/><b>{module.mastery}%</b></div><button onClick={()=>onStudy(module.featuredSessionSlug)}>Estudar</button></article>;
}

function StudyPage({slug,onBack}:{slug:string;onBack:()=>void}) {
  const [session,setSession]=useState<StudySession|null>(null);
  const [answers,setAnswers]=useState<Record<string,string>>({});
  const [result,setResult]=useState<StudySessionResult|null>(null);
  const [loading,setLoading]=useState(false);
  useEffect(()=>{ void getStudySession(slug).then(setSession); },[slug]);
  const completed=Object.keys(answers).length;
  const ready=session ? completed===session.questions.length : false;
  const progress=session ? Math.round(completed*100/session.questions.length) : 0;
  const resultMap=useMemo(()=>Object.fromEntries((result?.results??[]).map(r=>[r.questionId,r])),[result]);
  if(!session) return <div className="study-loading">Abrindo sessão...</div>;
  const submit=async()=>{setLoading(true);try{setResult(await submitStudySession(slug,answers));}finally{setLoading(false)}};
  return <div className="study-page">
    <aside className="study-nav"><button onClick={onBack}>← Voltar</button><div className="study-nav-title"><span>{session.category}</span><strong>{session.title}</strong></div><div className="study-progress-card"><small>Progresso da sessão</small><strong>{result?result.score:progress}%</strong><Progress value={result?result.score:progress}/></div><nav>{session.sections.map(s=><a key={s.id} href={`#${s.id}`}>{s.title.replace(/^\d+\. /,'')}</a>)}<a href="#quiz">Quiz final</a></nav></aside>
    <main className="study-main"><div className="study-top"><p className="eyebrow">SESSÃO DE ESTUDO</p><h1>{session.title}</h1><p>{session.subtitle}</p><div className="meta"><span>◷ {session.estimatedMinutes} min</span><span>★ Mastery {session.mastery}%</span></div></div>
      <div className="lesson-column">{session.sections.map(s=><article id={s.id} className="lesson-card" key={s.id}><span className="lesson-kicker">CONCEITO</span><h2>{s.title}</h2><p>{s.summary}</p><ul>{s.bullets.map(b=><li key={b}>{b}</li>)}</ul><div className="interview-tip"><b>◎ Dica de entrevista</b><span>{s.interviewTip}</span></div></article>)}
        <section id="quiz" className="quiz-section"><p className="eyebrow">QUIZ FINAL</p><h2>Teste seu domínio</h2><p>Responda todas as perguntas. O resultado atualiza seu mastery.</p>{session.questions.map((q,idx)=>{const qr=resultMap[q.id];return <article className="question-card" key={q.id}><div className="question-label"><span>Questão {idx+1}</span><b>{q.difficulty}</b></div><h3>{q.prompt}</h3><div className="options">{q.options.map(o=>{const selected=answers[q.id]===o.id;const cls=qr?(o.id===qr.correctOptionId?'correct':selected?'wrong':''):selected?'selected':'';return <button disabled={!!result} className={cls} key={o.id} onClick={()=>setAnswers(a=>({...a,[q.id]:o.id}))}><span>{o.id.toUpperCase()}</span>{o.label}</button>})}</div>{qr&&<div className={`answer-feedback ${qr.correct?'good':'bad'}`}><strong>{qr.correct?'Resposta correta':'Revisar este ponto'}</strong><span>{qr.explanation}</span></div>}</article>})}
        {!result?<div className="submit-box"><div><strong>{completed}/{session.questions.length} respondidas</strong><span>{ready?'Tudo pronto para avaliar.':'Complete o quiz para continuar.'}</span></div><button disabled={!ready||loading} onClick={submit}>{loading?'Avaliando...':'Concluir sessão'}</button></div>:<div className="result-box"><span>★</span><p className="eyebrow">SESSÃO CONCLUÍDA</p><strong>{result.score}%</strong><h3>{result.correct} de {result.total} corretas</h3><p>{result.message}</p><div className="mastery-change"><span>Mastery</span><b>{result.previousMastery}% → {result.newMastery}%</b></div><button onClick={onBack}>Voltar para a trilha</button></div>}
        </section>
      </div>
    </main>
  </div>;
}
