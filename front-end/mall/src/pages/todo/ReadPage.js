import { useCallback } from "react";
import { createSearchParams, useNavigate, useParams, useSearchParams } from "react-router-dom";
import ReadComponent from "../../components/todo/ReadComponent";

const ReadPage = () => {

  const {tno} = useParams()

  // const navigate = useNavigate()

  // const [queryParams] = useSearchParams()

  // const page = queryParams.get("page") ? parseInt(queryParams.get("page")) : 1
  // const size = queryParams.get("size") ? parseInt(queryParams.get("size")) : 10

  // const queryStr = createSearchParams({page,size}).toString()

  // const moveToModify = useCallback((tno) => {

  //   navigate({
  //     pathname: `/todo/modify/${tno}`,
  //     search: queryStr
  //   })

  // },[tno, page, size])

  // const moveToList = useCallback(() => {

  //   navigate({pathname:`/todo/list`, search: queryStr})
  // }, [page, size])


  return ( 
    <div className="font-extrabold w-full bg-white mt-6">
      <div className="text-2xl">

      Todo Read Page Component {tno}
      </div>
      <ReadComponent tno={tno}/>

    </div> 
  );

}
 
export default ReadPage;
