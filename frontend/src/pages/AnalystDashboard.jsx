import { useEffect, useState } from 'react'
import api from '../api/client'
import StatusPill from '../components/StatusPill'
import ScoreReadout from '../components/ScoreReadout'

export default function AnalystDashboard() {
  const [applications, setApplications] = useState([])
  const [loading, setLoading] = useState(true)
  const [selected, setSelected] = useState(null)
  const [scoreDetail, setScoreDetail] = useState(null)

  const [decisionStatus, setDecisionStatus] = useState('APPROVED')
  const [justification, setJustification] = useState('')
  const [offeredAmount, setOfferedAmount] = useState('')
  const [offeredTermMonths, setOfferedTermMonths] = useState('')
  const [decisionError, setDecisionError] = useState('')
  const [submittingDecision, setSubmittingDecision] = useState(false)
  const [successMessage, setSuccessMessage] = useState('')

  async function loadApplications() {
    setLoading(true)
    try {
      const { data } = await api.get('/applications')
      setApplications(data)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadApplications()
  }, [])

  async function openApplication(app) {
    setSelected(app)
    setScoreDetail(null)
    setJustification('')
    setDecisionError('')
    setSuccessMessage('')
    setDecisionStatus('APPROVED')
    setOfferedAmount('')
    setOfferedTermMonths('')
    try {
      const { data } = await api.get(`/applications/${app.id}/score`)
      setScoreDetail(data)
    } catch {
      setScoreDetail({ error: true })
    }
  }

  async function submitDecision(e) {
    e.preventDefault()
    setDecisionError('')
    setSubmittingDecision(true)
    try {
      const payload = { status: decisionStatus, justification }
      if (decisionStatus === 'COUNTER_OFFER') {
        payload.offeredAmount = Number(offeredAmount)
        payload.offeredTermMonths = Number(offeredTermMonths)
      }
      await api.patch(`/applications/${selected.id}/decision`, payload)
      setSuccessMessage('Decision recorded.')
      await loadApplications()
      setSelected(null)
    } catch (err) {
      setDecisionError(err.response?.data?.justification || err.response?.data?.error || 'Failed to record the decision')
    } finally {
      setSubmittingDecision(false)
    }
  }

  return (
    <div>
      <h1>Application queue</h1>
      <p>Review the automatic scoring result and make a decision, always with a justification.</p>

      {successMessage && <div className="alert alert-success">{successMessage}</div>}

      <div className="detail-grid">
        <div className="card">
          <div className="card-header">
            <h2>All applications</h2>
          </div>

          {loading && <div className="empty-state">Loading…</div>}
          {!loading && applications.length === 0 && (
            <div className="empty-state">No applications submitted yet.</div>
          )}

          {!loading && applications.map((app) => (
            <div
              key={app.id}
              className="list-row"
              style={{ cursor: 'pointer', background: selected?.id === app.id ? 'var(--surface-sunken)' : 'transparent' }}
              onClick={() => openApplication(app)}
            >
              <div className="row-main">
                <div className="row-title">{app.applicantFullName}</div>
                <div className="row-sub">#{app.id} · {app.requestedAmount} EUR</div>
              </div>
              <ScoreReadout score={app.totalScore} decision={app.decision} />
              <StatusPill status={app.status} />
            </div>
          ))}
        </div>

        <div className="card">
          <div className="card-header">
            <h2>Details</h2>
          </div>

          {!selected && <div className="empty-state">Select an application from the list on the left.</div>}

          {selected && (
            <div>
              <p style={{ fontSize: 13 }}>
                <strong>{selected.applicantFullName}</strong> ({selected.applicantEmail})<br />
                Income: {selected.monthlyIncome} EUR · Debt: {selected.monthlyDebt} EUR<br />
                Requesting: {selected.requestedAmount} EUR over {selected.termMonths} months
              </p>

              {scoreDetail && !scoreDetail.error && (
                <div style={{ marginBottom: 20 }}>
                  <h3>Score breakdown</h3>
                  {scoreDetail.details.map((d, idx) => (
                    <div key={idx} className={`rule-row ${d.triggered ? 'triggered' : ''}`}>
                      <div>
                        <div className="rule-name">{d.ruleName}</div>
                        <div className="rule-explanation">{d.explanation}</div>
                      </div>
                      <div className={`rule-points ${d.pointsContribution < 0 ? 'negative' : 'positive'}`}>
                        {d.pointsContribution > 0 ? '+' : ''}{d.pointsContribution}
                      </div>
                    </div>
                  ))}
                </div>
              )}

              <h3>Decision</h3>
              {decisionError && <div className="alert alert-error">{decisionError}</div>}

              <form onSubmit={submitDecision}>
                <div className="field">
                  <label htmlFor="decisionStatus">Status</label>
                  <select
                    id="decisionStatus"
                    value={decisionStatus}
                    onChange={(e) => setDecisionStatus(e.target.value)}
                  >
                    <option value="APPROVED">Approve</option>
                    <option value="REJECTED">Reject</option>
                    <option value="COUNTER_OFFER">Counter-offer</option>
                  </select>
                </div>

                {decisionStatus === 'COUNTER_OFFER' && (
                  <>
                    <div className="field">
                      <label htmlFor="offeredAmount">Offered amount (EUR)</label>
                      <input
                        id="offeredAmount"
                        type="number"
                        min="0"
                        step="0.01"
                        value={offeredAmount}
                        onChange={(e) => setOfferedAmount(e.target.value)}
                        required
                      />
                    </div>
                    <div className="field">
                      <label htmlFor="offeredTermMonths">Offered term (months)</label>
                      <input
                        id="offeredTermMonths"
                        type="number"
                        min="3"
                        max="120"
                        value={offeredTermMonths}
                        onChange={(e) => setOfferedTermMonths(e.target.value)}
                        required
                      />
                    </div>
                  </>
                )}

                <div className="field">
                  <label htmlFor="justification">Justification (required)</label>
                  <textarea
                    id="justification"
                    value={justification}
                    onChange={(e) => setJustification(e.target.value)}
                    required
                  />
                </div>

                <button className="btn btn-primary btn-block" type="submit" disabled={submittingDecision}>
                  {submittingDecision ? 'Saving…' : 'Save decision'}
                </button>
              </form>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
