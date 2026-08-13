import { useState } from "react";
import { UploadForm } from "./features/upload/UploadForm";
import "./styles.css";

export default function App() {
  const [token, setToken] = useState("");
  return <main className="shell">
    <header className="topbar"><a className="brand" href="/">STORAGE<span>·</span>ONE</a><span className="environment"><i /> Secure workspace</span></header>
    <section className="hero"><p className="eyebrow">Protected file delivery</p><h1>Upload with<br /><em>confidence.</em></h1><p className="hero-copy">A focused workspace for authenticated file transfer. Your JWT is held only in this browser session and is attached to every upload and download request.</p></section>
    <section className="workspace">
      <aside className="guide"><span className="step-number">01</span><h2>Connect your token</h2><p>Start the backend with the <code>local</code> profile, then paste the displayed <code>LOCAL_UPLOAD_JWT</code> value below.</p><div className="rule" /><span className="step-number">02</span><h2>Select and send</h2><p>Only JPEG, PNG and PDF files up to 10 MB are accepted. Server-side checks remain authoritative.</p></aside>
      <div className="upload-panel"><div className="panel-heading"><span className="eyebrow">New transfer</span><h2>Secure upload</h2></div><label className="token-field" htmlFor="jwt"><span>Bearer JWT</span><textarea id="jwt" value={token} onChange={event => setToken(event.target.value)} placeholder="Paste LOCAL_UPLOAD_JWT here for local testing" spellCheck={false} rows={3} /><small>Required for upload and protected download. It is never saved to local storage.</small></label><UploadForm token={token} /></div>
    </section>
    <footer>JWT protected · JPEG / PNG / PDF · 10 MB maximum</footer>
  </main>;
}
