const baseURL = "http://localhost:8080";

const createSubscription = async (feedUrl: string, recipientEmail: string) => {
  const path = `/subscription/subscribe`;
  const response = await fetch(`${baseURL}${path}`, {
    method: "POST",
    body: JSON.stringify({ feedUrl, recipientEmail }),
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
  });
  return response;
};

const validateSubscription = async (
  validationCode: string,
  subscriptionId: string
) => {
  const path = `/subscription/validate`;
  const response = await fetch(`${baseURL}${path}`, {
    method: "POST",
    body: JSON.stringify({ validationCode, subscriptionId }),
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
  });
  return response;
};

export { createSubscription, validateSubscription };
