package in.yousee.theadmin;

import in.yousee.theadmin.constants.RequestCodes;
import in.yousee.theadmin.constants.ServerFiles;
import in.yousee.theadmin.model.CustomException;

/**
 * Created by YouseeUC on 30-07-2016.
 */
public class LeavesMiddleware extends Middleware {

    public LeavesMiddleware(OnResponseReceivedListener listener) {
        super(listener);
    }

    @Override
    public void assembleRequest() {

    }

    public void applyLeave(String fromDate, String toDate, String typeOfLeave, String reasonForLeave){
        request.setUrl(NetworkConnectionHandler.DOMAIN + ServerFiles.LEAVE_APPLY);
        request.put("formdate", fromDate);
        request.put("todate", toDate);
        request.put("leavetype", typeOfLeave);
        request.put("reason", reasonForLeave);
        request.setRequestCode(RequestCodes.ACTIVITY_LEAVE_APPLY);

        try {
            sendRequest();
        } catch (CustomException e) {
            //TODO:show dialog
        }
    }

    @Override
    public void serveResponse(String result, int requestCode, int resultCode) {

    }
}
