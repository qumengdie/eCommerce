import { Pagination } from '@mui/material';
import { useNavigate, useSearchParams, useLocation } from 'react-router-dom';

const Paginations = ({ numberOfPage, totoalProducts }) => {
  const [searchParams] = useSearchParams();
  const params = new URLSearchParams(searchParams);
  const pathname = useLocation().pathname;
  const navigate = useNavigate();
  const paramValue = searchParams.get('page')
    ? Number(searchParams.get('page'))
    : 1;

  const onChangeHandler = (event, value) => {
    params.set('page', value.toString());
    navigate(`${pathname}?${params}`);
  };

  return (
    <Pagination
      count={numberOfPage}
      page={paramValue}
      defaultPage={1}
      siblingCount={1}
      boundaryCount={2}
      onChange={onChangeHandler}
    />
  );
};

export default Paginations;
