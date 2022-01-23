const baseURL = "http://localhost:8080";

const createSubscription = async (feedUrl: string, recipientEmail: string) => {
  const path = `/subscription/subscribe`;
  const response = await fetch(`${baseURL}${path}`, {
    method: "POST",
    body: JSON.stringify({ feedUrl, recipientEmail }),
    headers: {
      "Content-Type": "application/json",
    },
  });
  return response;
};

const validateSubscription = async (
  subscriptionId: string,
  validationCode: string
) => {
  const path = `/subscription/validate`;
  const response = await fetch(`${baseURL}${path}`, {
    method: "POST",
    body: JSON.stringify({ subscriptionId, validationCode }),
    headers: {
      "Content-Type": "application/json",
    },
  });
  return response;
};

export { createSubscription, validateSubscription };
