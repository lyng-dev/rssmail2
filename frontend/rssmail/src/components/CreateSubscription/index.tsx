import './style.scss'
import * as Yup from 'yup'
import { createSubscription, validateSubscription } from '../../services/api'

import { Formik, FormikHelpers, Form, Field, ErrorMessage } from 'formik'

interface Values {
  feedUrl: string
  recipientEmail: string
}

const subscriptionSchema = Yup.object().shape({
  feedUrl: Yup.string().url().required(),
  recipientEmail: Yup.string().email().required()
})

export const CreateSubscription = () => {

  const initialValues: Values = {
    feedUrl: '',
    recipientEmail: '',
  }

  const handleSubmit = async (values: Values, { setSubmitting }: FormikHelpers<Values>) => {
    const response = await createSubscription(values.feedUrl, values.recipientEmail)
    console.log(response.json().toString())
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