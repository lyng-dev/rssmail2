import './style.scss'
import * as Yup from 'yup'
import { createSubscription, checkFeed } from '../../services/api'

import { Formik, FormikHelpers, Form, Field, ErrorMessage } from 'formik'
import { useNavigate } from 'react-router-dom'

interface Values {
  feedUrl: string
  recipientEmail: string
}

const subscriptionSchema = Yup.object().shape({
  feedUrl: Yup.string()
  .required()
  .url()
  .test('valid feed', 'Specified feed url does not appear to be valid', 
    async (val) => {
      const response = await checkFeed(val ?? "")
      if (response.ok) return true;
      return false;
    }),
  recipientEmail: Yup.string().email().required()
})

export const CreateSubscription = () => {

  const navigate = useNavigate();

  const initialValues: Values = {
    feedUrl: '',
    recipientEmail: '',
  }

  const handleSubmit = async (values: Values, { setSubmitting }: FormikHelpers<Values>) => {
    const checkFeedResponse = await checkFeed(values.feedUrl)
    if (checkFeedResponse.ok) {
      const createSubscriptionResponse = await createSubscription(values.feedUrl, values.recipientEmail)
      if (createSubscriptionResponse.ok) {
        navigate('/createdsubscription')
      } else {
  
      }
    }
  }

  return (
    <>
      <Formik initialValues={initialValues} 
              validationSchema={subscriptionSchema} 
              onSubmit={(values, actions) => {
                  handleSubmit(values, actions)
              }}>
        <Form>
          RSS/Atom Feed Url: 
          <Field name="feedUrl" /> <ErrorMessage name="feedUrl" />
          Your e-mail address:
          <Field name="recipientEmail" /> <ErrorMessage name="recipientEmail" />
          <button type="submit">Subscribe Me!</button>
        </Form>
      </Formik>
    </>
  )
}