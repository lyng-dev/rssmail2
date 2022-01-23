import './style.scss'
import * as Yup from 'yup'
import { validateSubscription } from '../../services/api'

import { Formik, FormikHelpers, Form, Field, ErrorMessage } from 'formik'
import { useSearchParams } from 'react-router-dom'

interface Values {
  subscriptionId: string
  validationCode: string
}

const subscriptionSchema = Yup.object().shape({
  subscriptionId: Yup.string().required(),
  validationCode: Yup.string().required()
})

export const ValidateSubscription = () => {

  const [searchParams] = useSearchParams();
  const subscriptionId = searchParams.get('subscriptionId')
  const validationCode = searchParams.get('validationCode');

  const initialValues: Values = {
    subscriptionId: subscriptionId ?? "", 
    validationCode: validationCode ?? ""
  }

  const handleSubmit = async (values: Values, { setSubmitting }: FormikHelpers<Values>) => {
    console.log(values.subscriptionId);
    console.log(values.validationCode);
    const response = await validateSubscription(values.subscriptionId, values.validationCode)
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
          <Field name="subscriptionId" type="hidden" value={initialValues.subscriptionId} /> <ErrorMessage name="subscriptionId" />
          <Field name="validationCode" type="hidden" value={initialValues.validationCode} /> <ErrorMessage name="validationCode" />
          <button type="submit">Validate</button>
        </Form>
      </Formik>
    </>
  )
}