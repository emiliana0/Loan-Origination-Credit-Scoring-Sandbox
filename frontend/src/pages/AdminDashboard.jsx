import { useEffect, useState } from 'react'
import api from '../api/client'

export default function AdminDashboard() {
  const [rules, setRules] = useState([])
  const [loading, setLoading] = useState(true)
  const [editingId, setEditingId] = useState(null)
  const [draft, setDraft] = useState({})
  const [error, setError] = useState('')
  const [successMessage, setSuccessMessage] = useState('')

  async function loadRules() {
    setLoading(true)
    try {
      const { data } = await api.get('/scoring-rules')
      setRules(data)
    } catch {
      setError('Failed to load scoring rules')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadRules()
  }, [])

  function startEdit(rule) {
    setEditingId(rule.id)
    setDraft({
      thresholdValue: rule.thresholdValue,
      pointsImpact: rule.pointsImpact,
      active: rule.active
    })
    setSuccessMessage('')
  }

  async function saveEdit(id) {
    setError('')
    try {
      await api.put(`/scoring-rules/${id}`, {
        thresholdValue: Number(draft.thresholdValue),
        pointsImpact: Number(draft.pointsImpact),
        active: draft.active
      })
      setSuccessMessage('Rule updated.')
      setEditingId(null)
      await loadRules()
    } catch {
      setError('Failed to save the rule')
    }
  }

  return (
    <div>
      <h1>Scoring rules</h1>
      <p>Adjust thresholds and points without touching the application code.</p>

      {error && <div className="alert alert-error">{error}</div>}
      {successMessage && <div className="alert alert-success">{successMessage}</div>}

      <div className="card">
        {loading && <div className="empty-state">Loading…</div>}

        {!loading && rules.map((rule) => (
          <div key={rule.id} className="list-row" style={{ alignItems: 'flex-start' }}>
            <div className="row-main">
              <div className="row-title">{rule.name}</div>
              <div className="row-sub" style={{ fontFamily: 'var(--font-body)', color: 'var(--muted)' }}>
                {rule.description}
              </div>

              {editingId === rule.id ? (
                <div style={{ display: 'flex', gap: 12, marginTop: 10, alignItems: 'flex-end', flexWrap: 'wrap' }}>
                  <div className="field" style={{ margin: 0, width: 120 }}>
                    <label>Threshold</label>
                    <input
                      type="number"
                      step="0.01"
                      value={draft.thresholdValue}
                      onChange={(e) => setDraft({ ...draft, thresholdValue: e.target.value })}
                    />
                  </div>
                  <div className="field" style={{ margin: 0, width: 100 }}>
                    <label>Points</label>
                    <input
                      type="number"
                      value={draft.pointsImpact}
                      onChange={(e) => setDraft({ ...draft, pointsImpact: e.target.value })}
                    />
                  </div>
                  <div className="field" style={{ margin: 0 }}>
                    <label>Active</label>
                    <input
                      type="checkbox"
                      checked={draft.active}
                      onChange={(e) => setDraft({ ...draft, active: e.target.checked })}
                      style={{ width: 'auto' }}
                    />
                  </div>
                  <button className="btn btn-primary" onClick={() => saveEdit(rule.id)}>Save</button>
                  <button className="btn btn-secondary" onClick={() => setEditingId(null)}>Cancel</button>
                </div>
              ) : (
                <div style={{ marginTop: 6, fontFamily: 'var(--font-mono)', fontSize: 12, color: 'var(--ink-soft)' }}>
                  threshold {rule.thresholdValue} · points {rule.pointsImpact > 0 ? '+' : ''}{rule.pointsImpact} ·{' '}
                  {rule.active ? 'active' : 'disabled'}
                </div>
              )}
            </div>

            {editingId !== rule.id && (
              <button className="btn btn-secondary" onClick={() => startEdit(rule)}>Edit</button>
            )}
          </div>
        ))}
      </div>
    </div>
  )
}
