import { useEffect, useState } from 'react'
import api from '../api/client'
import StatusPill from '../components/StatusPill'
import ScoreReadout from '../components/ScoreReadout'

export default function ApplicantDashboard() {
  const [applications, setApplications] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [selectedId, setSelectedId] = useState(null)
  const [scoreDetail, setScoreDetail] = useState(null)

  const [form, setForm] = useState({
    monthlyIncome: '',
    monthlyDebt: '',
    termMonths: '',
    requestedAmount: ''
  })
  const [formErrors, setFormErrors] = useState({})

  async function loadApplications() {
    setLoading(true)
    try {
      const { data } = await api.get('/applications/my')
      setApplications(data)
    } catch (err) {
      setError('Failed to load your applications')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadApplications()
  }, [])

  function updateField(field, value) {
    setForm((prev) => ({ ...prev, [field]: value }))
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setFormErrors({})
    setSubmitting(true)
    try {
      await api.post('/applications', {
        monthlyIncome: Number(form.monthlyIncome),
        monthlyDebt: Number(form.monthlyDebt),
        termMonths: Number(form.termMonths),
        requestedAmount: Number(form.requestedAmount)
      })
      setForm({ monthlyIncome: '', monthlyDebt: '', termMonths: '', requestedAmount: '' })
      await loadApplications()
    } catch (err) {
      if (err.response?.status === 400 && typeof err.response.data === 'object') {
        setFormErrors(err.response.data)
      } else {
        setError('Failed to submit the application')
      }
    } finally {
      setSubmitting(false)
    }
  }

  async function viewDetail(id) {
    setSelectedId(id)
    setScoreDetail(null)
    try {
      const { data } = await api.get(`/applications/${id}/score`)
      setScoreDetail(data)
    } catch (err) {
      setScoreDetail({ error: true })
    }
  }

  return (
    <div>
      <h1>My applications</h1>
      <p>Submit a new loan application and track the status of your existing ones.</p>

      <div className="detail-grid">
        <div className="card">
          <div className="card-header">
            <h2>New application</h2>
          </div>

          {error && <div className="alert alert-error">{error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="field">
              <label htmlFor="monthlyIncome">Monthly income (EUR)</label>
              <input
                id="monthlyIncome"
                type="number"
                min="0"
                step="0.01"
                value={form.monthlyIncome}
                onChange={(e) => updateField('monthlyIncome', e.target.value)}
                required
              />
              {formErrors.monthlyIncome && <div className="error-text">{formErrors.monthlyIncome}</div>}
            </div>

            <div className="field">
              <label htmlFor="monthlyDebt">Monthly debt payments (EUR)</label>
              <input
                id="monthlyDebt"
                type="number"
                min="0"
                step="0.01"
                value={form.monthlyDebt}
                onChange={(e) => updateField('monthlyDebt', e.target.value)}
                required
              />
              {formErrors.monthlyDebt && <div className="error-text">{formErrors.monthlyDebt}</div>}
            </div>

            <div className="field">
              <label htmlFor="termMonths">Term (months)</label>
              <input
                id="termMonths"
                type="number"
                min="3"
                max="120"
                value={form.termMonths}
                onChange={(e) => updateField('termMonths', e.target.value)}
                required
              />
              {formErrors.termMonths && <div className="error-text">{formErrors.termMonths}</div>}
            </div>

            <div className="field">
              <label htmlFor="requestedAmount">Requested amount (EUR)</label>
              <input
                id="requestedAmount"
                type="number"
                min="0"
                step="0.01"
                value={form.requestedAmount}
                onChange={(e) => updateField('requestedAmount', e.target.value)}
                required
              />
              {formErrors.requestedAmount && <div className="error-text">{formErrors.requestedAmount}</div>}
            </div>

            <button className="btn btn-primary btn-block" type="submit" disabled={submitting}>
              {submitting ? 'Submitting…' : 'Submit application'}
            </button>
          </form>
        </div>

        <div className="card">
          <div className="card-header">
            <h2>History</h2>
          </div>

          {loading && <div className="empty-state">Loading…</div>}

          {!loading && applications.length === 0 && (
            <div className="empty-state">You haven't submitted any applications yet.</div>
          )}

          {!loading && applications.map((app) => (
            <div key={app.id}>
              <div className="list-row" style={{ cursor: 'pointer' }} onClick={() => viewDetail(app.id)}>
                <div className="row-main">
                  <div className="row-title">{app.requestedAmount} EUR · {app.termMonths} mo.</div>
                  <div className="row-sub">#{app.id}</div>
                </div>
                <ScoreReadout score={app.totalScore} decision={app.decision} />
                <StatusPill status={app.status} />
              </div>

              {selectedId === app.id && scoreDetail && !scoreDetail.error && (
                <div style={{ padding: '8px 0 16px' }}>
                  {app.status === 'COUNTER_OFFER' && (
                    <div className="alert alert-success">
                      Counter-offer: {app.offeredAmount} EUR over {app.offeredTermMonths} months
                    </div>
                  )}
                  {app.overrideJustification && (
                    <div className="alert alert-success">
                      Analyst note: {app.overrideJustification}
                    </div>
                  )}
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
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}
