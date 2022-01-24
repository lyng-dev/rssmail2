import './style.scss'
import * as Yup from 'yup'
import { deleteSubscription } from '../../services/api'

import { Formik, FormikHelpers, Form, Field, ErrorMessage } from 'formik'
import { useNavigate, useSearchParams } from 'react-router-dom'

interface Values {
  subscriptionId: string
  recipientEmail: string
}

const subscriptionSchema = Yup.object().shape({
  subscriptionId: Yup.string().required(),
  recipientEmail: Yup.string().required()
})

export const DeleteSubscription = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const subscriptionId = searchParams.get('subscriptionId')
  const recipientEmail = searchParams.get('recipientEmail');

  const initialValues: Values = {
    subscriptionId: subscriptionId ?? "", 
    recipientEmail: recipientEmail ?? ""
  }

  const handleSubmit = async (values: Values, { setSubmitting }: FormikHelpers<Values>) => {
    const response = await deleteSubscription(values.subscriptionId, values.recipientEmail)
    navigate('/deletedsubscription')
  }

  return (
    <>
      <Formik initialValues={initialValues} 
              validationSchema={subscriptionSchema} 
              onSubmit={(values, actions) => {
                  handleSubmit(values, actions)
              }}>
        <Form>
        <>Delete your subsription: (id={initialValues.subscriptionId})</>                
          <Field name="subscriptionId" type="hidden" value={initialValues.subscriptionId} /> <ErrorMessage name="subscriptionId" />
          <Field name="recipientEmail" type="hidden" value={initialValues.recipientEmail} /> <ErrorMessage name="validationCode" />
          <><button type="submit">Delete</button></>
        </Form>
      </Formik>
    </>
  )
}