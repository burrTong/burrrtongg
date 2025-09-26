import React, { useState } from 'react';

const ContactPage = () => {
  const [submitted, setSubmitted] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    setSubmitted(true);
  };

  return (
    <div>
      <h2>Contact Us</h2>
      {submitted ? (
        <div data-test="contact-success-message">Your message has been sent!</div>
      ) : (
        <form data-test="contact-form" onSubmit={handleSubmit}>
          <div>
            <label htmlFor="subject">Subject</label>
            <input type="text" id="subject" data-test="contact-subject" />
          </div>
          <div>
            <label htmlFor="message">Message</label>
            <textarea id="message" data-test="contact-message"></textarea>
          </div>
          <button type="submit" data-test="contact-submit">Send</button>
        </form>
      )}
    </div>
  );
};

export default ContactPage;
