const LABELS = {
  SUBMITTED: 'Submitted',
  IN_REVIEW: 'In review',
  APPROVED: 'Approved',
  REJECTED: 'Rejected',
  COUNTER_OFFER: 'Counter-offer'
}

export default function StatusPill({ status }) {
  if (!status) return null
  const className = status.toLowerCase()
  return (
    <span className={`status-pill ${className}`}>
      {LABELS[status] || status}
    </span>
  )
}
