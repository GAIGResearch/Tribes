from os import listdir
from os.path import isfile, join
import numpy as np
import matplotlib.pyplot as plt
import pylab
import matplotlib

linestiles = ['-', '--', '-.', ':']

agent_names = ["DoNothing", "Random", "RuleBased", "OSLA", "MC", "MCTS", "RHEA", "OEP"]
agents_dict = {"DoNothing" : "DoNothing", "Random" : "Random", "SimpleAgent" : "RuleBased", "OneStepLookAheadAgent" : "OSLA",
               "MonteCarloAgent" : "MC", "MCTSPlayer":"MCTS", "RHEAAgent":"RHEA", "OEP":"OEP"}

game_modes = ["CAPITALS", "SCORE"]


TURNS = 51


def errorfill(x, y, yerr, lnst='-', color=None, alpha_fill=0.3, ax=None):
    ax = ax if ax is not None else plt.gca()
    if color is None:
        color = ax._get_lines.color_cycle.next()
    if np.isscalar(yerr) or len(yerr) == len(y):
        ymin = [a - b for a,b in zip(y, yerr)]
        ymax = [sum(pair) for pair in zip(y, yerr)]
    elif len(yerr) == 2:
        ymin, ymax = yerr

    ax.plot(x, y, color=color, linestyle=lnst)
    ax.fill_between(x, ymax, ymin, color=color, alpha=alpha_fill)


def process_file(f_name, max_data = 200000, bounds = [0, TURNS]):

    with open(f_name) as f:
        lines = f.readlines()
        rep = 0
        skipTillNext = False
        dataCount = 0
        game_data = [[] for _ in range(6)]
        all_game_data = []
        theresData = False

        for line in lines:

            if len(all_game_data) == max_data:
                return all_game_data

            if "Playing with" in line:

                if rep > 0 and theresData is True and skipTillNext is False:
                    all_game_data.append(game_data)

                rep = rep + 1
                skipTillNext = False
                dataCount = 0
                game_data = [[] for _ in range(6)]
                theresData = False

            if "Game Results" in line:
                turns = int(line.split(";")[0])
                if turns < TURNS:
                    skipTillNext = True

            if skipTillNext is False and ("Branching factor" in line or "moves in turn:" in line or "Actions Per Step Avg" in line):

                if "Branching factor" in line:
                    theresData = True
                    spaces = line.split(",")[2].split(" ")
                    for idx in range(len(spaces)):
                        if idx >= bounds[0] and idx <= bounds[1]:
                            d = spaces[idx]
                            if len(d) > 0 and d != '\n':
                                game_data[dataCount].append(float(d))

                if "moves in turn:" in line:
                    theresData = True
                    moves = line.split(",")[2].split(" ")
                    for idx in range(len(moves)):
                        if idx >= bounds[0] and idx <= bounds[1]:
                            m = moves[idx]
                            if len(m) > 0 and m != '\n':
                                game_data[dataCount].append(int(m))


                if "Actions Per Step Avg" in line:
                    theresData = True
                    aps = line.split(",")[2].split(" ")
                    for idx in range(len(aps)):
                        if idx >= bounds[0] and idx <= bounds[1]:
                            a = aps[idx]
                            if len(a) > 0 and a != '\n':
                                game_data[dataCount].append(float(a))

                dataCount = dataCount + 1

                # elif "Actions Per Step" in line:
                #
                #     turns = int(line.split(",")[1])
                #     if turns < TURNS:
                #         skipTillNext = True
                #
                #     aps = line.split(",")[2].split(" ")
                #     for idx in range(len(aps)):
                #         if idx >= bounds[0] and idx <= bounds[1]:
                #             a = aps[idx]
                #             if len(a) > 0 and a != '\n':
                #                 game_data[dataCount].append(float(a))




    return all_game_data


def drawPlot(series, xlab, ylab, legend, title, outputFile, series_err=None, showPlot=True,
             saveToFile=False):
    # Create a figure
    fig = pylab.figure()

    font = {'family': 'normal',
            'size': 14}

    matplotlib.rc('font', **font)

    # Add a subplot (Grid of plots 1x1, adding plot 1)
    ax = fig.add_subplot(111)

    rects = [x for x in range(len(series[0]))]

    for i in range(len(series)):
        errorfill(range(0, len(rects)), series[i], series_err[i], linestiles[i])

    plt.xticks(np.arange(0, 51, step=5))
    ax.set_xticklabels(np.arange(0, 51, step=5))

    # Titles and labels
    plt.title(title)
    plt.xlabel(xlab)
    plt.ylabel(ylab)
    plt.grid()

    plt.legend(legend, loc='upper left')


    if saveToFile:
        fig.savefig(outputFile)

    # And show it:
    if showPlot:
        plt.show()

def getTrends(f, max_data, indices = [0,2,1,3], apply_log10 = True, bounds = [0,TURNS]):
    all_game_data = process_file(f, max_data, bounds)
    samples = len(all_game_data)
    series_length = len(all_game_data[0][0])

    bf_win = [[] for _ in range(series_length)]
    bf_win_avg = []
    bf_win_std = []
    bf_lose = [[] for _ in range(series_length)]
    bf_lose_avg = []
    bf_lose_std = []
    moves_win = [[] for _ in range(series_length)]
    moves_win_avg = []
    moves_win_std = []
    moves_lose = [[] for _ in range(series_length)]
    moves_lose_avg = []
    moves_lose_std = []

    for game_data in all_game_data:
        # print game_data[0][t] + " " + str(np.log10())
        for t in range(series_length):

            if apply_log10:
                # print game_data
                bf_win[t].append(np.log10(game_data[indices[0]][t]))
                bf_lose[t].append(np.log10(game_data[indices[1]][t]))
            else:
                bf_win[t].append(game_data[indices[0]][t])
                bf_lose[t].append(game_data[indices[1]][t])

            moves_win[t].append(game_data[indices[2]][t])
            moves_lose[t].append(game_data[indices[3]][t])

    for t in range(len(bf_win)):
        bf_win_avg.append(np.average(bf_win[t]))
        bf_win_std.append(bf_win_avg[t] / np.sqrt(samples))

        bf_lose_avg.append(np.average(bf_lose[t]))
        bf_lose_std.append(bf_lose_avg[t] / np.sqrt(samples))

        moves_win_avg.append(np.average(moves_win[t]))
        moves_win_std.append(moves_win_avg[t] / np.sqrt(samples))

        moves_lose_avg.append(np.average(moves_lose[t]))
        moves_lose_std.append(moves_lose_avg[t] / np.sqrt(samples))

    bfs = [bf_win_avg, bf_win_std, bf_lose_avg, bf_lose_std]
    moves = [moves_win_avg, moves_win_std, moves_lose_avg, moves_lose_std]

    print "Num samples: " + str(samples)
    print "Avg trend1[0]: " + str(np.average(bf_win_avg))
    print "Avg trend1[1]: " + str(np.average(bf_lose_avg))
    print "Avg trend1[0+1]: " + str((np.average(bf_win_avg) + np.average(bf_lose_avg)) / 2.0)
    print "Avg trend2[0]: " + str(np.average(moves_win_avg))
    print "Avg trend2[1]: " + str(np.average(moves_lose_avg))
    print "Avg trend2[0+1]: " + str((np.average(moves_win_avg) + np.average(moves_lose_avg)) / 2.0)
    # print bf_win_avg
    # print all_game_data

    return bfs, moves

def main():

    # f = "../../res/action_spaces.txt"
    # f = "/Users/dperez/sandbox/2/action_spaces.txt"
    # bfs, moves = getTrends(f, 200)
    # f = "/Users/dperez/sandbox/2/action_spaces_mean.txt"
    # bfs_mean, moves2 = getTrends(f,200)
    f = "/Users/dperez/Work/git/Tribes/res/all.txt"

    # drawPlot([bfs[0], bfs[2]], 'Turns', 'Action Space Size (log-10 scale)',
    #          ['ABF Winner', 'ABF Loser'], 'Action Space', 'output.png',
    #          [bfs[1], bfs[3]])
    #
    # drawPlot([bfs[0], bfs_mean[0]], 'Turns', 'Action Space Size (log-10 scale)',
    #          ['ABF Winner (Max)', 'ABF Winner (Mean)'], 'Action Space', 'output.png',
    #          [bfs[1], bfs_mean[1]])


    # drawPlot([bfs[0], bfs_mean[0], bfs[2], bfs_mean[2]], 'Turns', 'Size (log-10 scale)',
    #          ['Win Player (Max per turn)', 'Win Player (Mean per turn)', 'Lose Player (Max per turn)', 'Lose Player (Mean per turn)'], 'Average Action Space Size', 'actionBranchingFactor.png',
    #          [bfs[1], bfs_mean[1], bfs[3], bfs_mean[3]], True, True)

    # drawPlot([moves[0], moves[2]], 'Turns', 'Number of Moves', ['Avg Moves Win Player', 'Avg Moves Lose Player'], 'Average Moves Played per Turn', 'movesPlayed.png', [moves[1], moves[3]], True, True)

    # trend1, trend2 = getTrends(f, 200, [0,1,3,4], False)
    # drawPlot([trend1[0], trend1[2], trend2[0], trend2[2]], 'Turns', 'Actions per Step',
    #          ['Win Player (APS)', 'Win Player (Avg APS)', 'Lose Player (APS)', 'Lose Player (Avg APS)'], 'Average Actions per Step', 'actionsPerStep.png',
    #          [trend1[1], trend1[3], trend2[1], trend2[3]], True, True)

    # trend1, trend2 = getTrends(f, 200, [0,2,3,5], False)
    # drawPlot([trend1[0], trend1[2], trend2[0], trend2[2]], 'Turns', 'Actions and Moves per Step',
    #          ['Win Player (APS)', 'Win Player (Moves)', 'Lose Player (APS)', 'Lose Player (Moves)'], 'Average Actions per Step', 'actionsAndMovesPerStep.png',
    #          [trend1[1], trend1[3], trend2[1], trend2[3]], True, True)


    # bounds = [0, 51]
    # bounds = [0,25]
    bounds = [46,51]
    trend1, trend2 = getTrends(f, 400, [1, 4, 0, 3], True, bounds)


    drawPlot([trend1[0], trend1[2]], 'Turns', 'Branching Factor per Turn (log-10 scale)',
             ['Winning', 'Losing'], 'Average Branching Factor per Turn', 'branchingFactorTurn.png',
             [trend1[1], trend1[3]], True, True)


    drawPlot([trend2[0], trend2[2]], 'Turns', 'Available Actions per Move',
             ['Winning', 'Losing'], 'Average Available Actions per Move', 'branchingFactorStep.png',
             [trend2[1], trend2[3]], True, True)


    moves, a = getTrends(f, 200, [2, 5, 0, 0], False, bounds)
    drawPlot([moves[0], moves[2]], 'Turns', 'Number of Moves', ['Avg Moves Win Player', 'Avg Moves Lose Player'], 'Average Moves Played per Turn', 'movesPlayed.png', [moves[1], moves[3]], True, True)

main()