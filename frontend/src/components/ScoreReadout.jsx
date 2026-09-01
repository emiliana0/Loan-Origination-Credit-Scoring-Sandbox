// The one visual element repeated across every screen - a compact
// "ledger readout" of the scoring engine's number and decision, so an
// applicant, analyst, and admin all read the same language at a glance.
export default function ScoreReadout({ score, decision }) {
  if (score === null || score === undefined) {
    return <span className="score-readout"><span className="score-number">—</span></span>
  }

  const decisionClass = decision ? decision.toLowerCase() : ''

  return (
    <span className={`score-readout ${decisionClass}`}>
      <span className="score-number">{score}</span>
      <span className="score-bar">
        <span className="score-bar-fill" style={{ width: `${score}%` }} />
      </span>
    </span>
  )
}
