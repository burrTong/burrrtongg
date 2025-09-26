import React from 'react';

const AdminSupportTickets = () => {
  return (
    <div>
      <h2>Admin Support Tickets</h2>
      <div data-test="ticket-row">
        <span>Ticket Subject</span>
      </div>
      <div data-test="ticket-details">
        <textarea data-test="ticket-reply-textarea"></textarea>
        <button data-test="ticket-reply-button">Reply</button>
      </div>
      <div data-test="reply-sent-confirmation">Reply Sent</div>
    </div>
  );
};

export default AdminSupportTickets;
